🚀 智慧医药系统 (Smart Medicine System)
🌟 项目简介
本项目是一个基于 Spring Boot 3.x 的医药信息管理与服务平台,原创作者为：薛伟同学,然后我进行升级改造,核心目标是构建一个具备 高并发挂号 和 高性能搜索 能力的企业级后端系统。




✨ 已完成的重构与优化 (Day 1 )：
1. 挂号模块：高并发与高一致性重构 (QPS 提升了 >= 50/sec)痛点：原有数据库事务锁存在严重的 超卖风险 和 性能瓶颈 (Day 4 QPS 仅 140/sec)。解决方案：缓存预热：系统启动时将库存数据从 MySQL 预热到 Redis。原子扣减：在 BookingService 中，使用 Redis DECR 原子操作 瞬间拦截 99% 的流量，保证库存不会出现负数。数据库兜底：在 Redis 扣减成功后，立即使用 MySQL 原生 SQL 原子更新 (UPDATE ... WHERE available_stock > 0) 进行最终扣减，作为数据一致性的最后一道屏障。事务保护：通过 @Transactional 确保数据库操作失败时，库存和订单能正确回滚，并同时执行 Redis 的库存回滚。成果：成功实现 0.00% 错误率，并使 QPS 提升至 190 以上。
2. 核心兼容性修复将项目从旧的 Spring Boot 2.x 依赖全部升级到 Spring Boot 3.x，并解决了 Jakarta EE 规范 冲突、Lombok 版本不兼容等近十个环境启动问题。
