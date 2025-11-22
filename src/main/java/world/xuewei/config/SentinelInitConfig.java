package world.xuewei.config;

import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import jakarta.annotation.PostConstruct; // 导入

import java.util.ArrayList;
import java.util.List;

@Configuration
@Slf4j
public class SentinelInitConfig {

    @PostConstruct
    public void initFlowRules() {
        // 1. 定义流量控制规则列表
        List<FlowRule> rules = new ArrayList<>();

        // 2. 创建 FlowRule 对象
        FlowRule rule = new FlowRule();
        rule.setResource("bookingFlowRule"); // 【必须】与 @SentinelResource 的 value 匹配
        rule.setGrade(RuleConstant.FLOW_GRADE_QPS); // 设置为 QPS 阈值类型
        rule.setCount(5); // 【核心】设置 QPS 阈值为 5

        rules.add(rule);

        // 3. 加载规则到 Sentinel 系统
        FlowRuleManager.loadRules(rules);
        log.info(">>> Sentinel 规则已通过 Java 代码强制加载，QPS 阈值设置为 5。");
    }
}