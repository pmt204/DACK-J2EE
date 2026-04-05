package com.example.ticketbooking.controller;

import com.example.ticketbooking.config.VNPayConfig;
import com.example.ticketbooking.entity.Booking;
import com.example.ticketbooking.repository.BookingRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final BookingRepository bookingRepository;
    private final VNPayConfig vnPayConfig;

    /**
     * Nút "Thanh toán VNPay" ở giao diện sẽ gọi vào URL này.
     * Cần truyền lên bookingId.
     */
    @GetMapping("/create-payment")
    public String createPayment(HttpServletRequest request, @RequestParam Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy booking"));

        // Số tiền phải nhân với 100 theo quy định của VNPAY (vd: 100k -> 10000000)
        long amount = booking.getTotalPrice().longValue() * 100;
        String vnp_TxnRef = VNPayConfig.getRandomNumber(8); // Mã giao dịch ngẫu nhiên

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", "2.1.0");
        vnp_Params.put("vnp_Command", "pay");
        vnp_Params.put("vnp_TmnCode", vnPayConfig.vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(amount));
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_BankCode", "NCB"); // Dùng ngân hàng NCB để test
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", "Thanh toan ve may bay ma: " + bookingId);
        vnp_Params.put("vnp_OrderType", "other");
        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_ReturnUrl", vnPayConfig.vnp_ReturnUrl + "?bookingId=" + bookingId);
        vnp_Params.put("vnp_IpAddr", VNPayConfig.getIpAddress(request));

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        // Build chuỗi dữ liệu (query string) và mã hóa Hash
        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator<String> itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = itr.next();
            String fieldValue = vnp_Params.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                // Build hash data
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                // Build query
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII));
                query.append('=');
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }

        String queryUrl = query.toString();
        String vnp_SecureHash = VNPayConfig.hmacSHA512(vnPayConfig.secretKey, hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
        String paymentUrl = vnPayConfig.vnp_PayUrl + "?" + queryUrl;

        // Chuyển hướng người dùng tới trang thanh toán của VNPAY
        return "redirect:" + paymentUrl;
    }

    /**
     * VNPAY sẽ gọi lại (redirect) về URL này sau khi người dùng thanh toán xong.
     */
    @GetMapping("/vnpay-return")
    public String paymentReturn(@RequestParam Map<String, String> params, @RequestParam Long bookingId) {
        String vnp_ResponseCode = params.get("vnp_ResponseCode");

        // Code "00" của VNPAY nghĩa là giao dịch thành công
        if ("00".equals(vnp_ResponseCode)) {
            Booking booking = bookingRepository.findById(bookingId).orElseThrow();
            booking.setPaymentStatus("PAID");
            bookingRepository.save(booking);
            // Thành công: Redirect về trang lịch sử vé hoặc trang báo thành công
            return "redirect:/bookings/my-bookings?payment=success";
        } else {
            // Thất bại hoặc hủy giao dịch
            return "redirect:/bookings/my-bookings?payment=failed";
        }
    }
}