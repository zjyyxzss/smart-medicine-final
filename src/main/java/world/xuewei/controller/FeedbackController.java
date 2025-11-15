package world.xuewei.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import world.xuewei.entity.Feedback;

import world.xuewei.service.FeedbackService;

/**
 * 反馈控制器
 *
 *
 */
@RestController
@RequestMapping(value = "feedback")
public class FeedbackController extends BaseController<Feedback> {

    @Autowired
    private FeedbackService feedbackService;



}