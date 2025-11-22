package world.xuewei.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import world.xuewei.dto.RespResult;
import world.xuewei.service.BookingService;

/**
 * 预约控制器
 *
 *
 */
@RestController
@RequestMapping("/api/booking")
public class BookingController {
    @Autowired
    private BookingService bookingService;
    /**
     * 用户预约医生
     *
     * @param scheduleId 排班ID
     * @return 预约结果，包含成功或失败信息
     */
    @PostMapping("book")
    public RespResult book(@RequestParam Long scheduleId, @RequestParam Long userId) {
        //Long userId = 1L;
        return bookingService.book(userId, scheduleId);
    }
}
