//package pers.tgl.mikufans.config;
//
//import org.springframework.amqp.core.*;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
///**
// * 配置 rabbitMQ
// *     public static final String QUEUE = "miku.queue.es_user";
// *
// *     private final Queue queue = new Queue(QUEUE, true);
// *
// *     @Bean(QUEUE)
// *     public Queue queue() {
// *         return queue;
// *     }
// *
// *     @Bean("binding_" + QUEUE)
// *     public Binding binding(DirectExchange exchange) {
// *         return BindingBuilder.bind(queue)
// *                 .to(exchange)
// *                 .with(QUEUE);
// *     }
// *
// *     @RabbitListener(queues = QUEUE)
// *     public void listen(String userId) throws IOException {
// *     }
// */
//@Configuration
//public class MqConfig {
//    public static final String PREFIX = "miku.";
//
//    /**
//     * 路由模式,发送消息时通过Routing到达特定的队列
//     */
//    @Bean
//    public DirectExchange directExchange() {
//        return new DirectExchange(PREFIX + ExchangeTypes.DIRECT, true, false);
//    }
//
//    /**
//     * 广播消息到所有队列
//     */
//    @Bean
//    public FanoutExchange fanoutExchange() {
//        return new FanoutExchange(PREFIX + ExchangeTypes.FANOUT, true, false);
//    }
//
//    @Bean
//    public TopicExchange topicExchange() {
//        return new TopicExchange(PREFIX + ExchangeTypes.TOPIC, true, false);
//    }
//}