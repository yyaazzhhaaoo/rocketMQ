package com.myz.rocketmq;

import com.myz.rocketmq.service.bean.OrderModel;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.*;
import org.apache.rocketmq.client.producer.*;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Unit test for simple App.
 */
public class AppTest extends TestCase {
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest(String testName) {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(AppTest.class);
    }

    /**
     * Rigourous Test :-)
     */
    public void testContextLoads() throws Exception {
        //创建一个生产者
        DefaultMQProducer producer = new DefaultMQProducer("test-producer-group");
        //连接namesrv
        producer.setNamesrvAddr("192.168.5.72:9876");
        //启动生产者
        producer.start();

        Message message = new Message("test-topic", "我是一个简单的消息1".getBytes(StandardCharsets.UTF_8));
        SendResult result = producer.send(message);
        SendStatus sendStatus = result.getSendStatus();
        System.out.println(sendStatus);
        //关闭生产者
        producer.shutdown();
    }

    public void testSimpleProducer() throws Exception {
        //创建一个消费者
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("test-consumer-group");
        consumer.setNamesrvAddr("192.168.5.73:9876");
        //订阅一个主题
        consumer.subscribe("batchTopic", "*");
        //设置一个监听器
        consumer.registerMessageListener((MessageListenerConcurrently) (list, context) -> {
            System.out.println("我是消费者");
            for (MessageExt messageExt : list) {
                String s = new String(messageExt.getBody(), StandardCharsets.UTF_8);
                System.out.println(">>>>>" + s);

            }
            System.out.println("消费上下文：" + context);

            /*
              CONSUME_SUCCESS:消息会从mq出队
              RECONSUME_LATER:（报错/null）失败，消息会重新回到队列，过一会儿重新投递出来
             */
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        });
        //启动消费者
        consumer.start();

        System.in.read();
    }

    public void testAsyncProducer() throws Exception {
        //创建一个生产者
        DefaultMQProducer producer = new DefaultMQProducer("test-producer-group");
        //连接namesrv
        producer.setNamesrvAddr("192.168.5.73:9876");
        //启动生产者
        producer.start();

        Message message = new Message("async-topic", "我是一个异步消息发送者".getBytes(StandardCharsets.UTF_8));
        producer.send(message, new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                System.out.println("发送成功");
            }

            @Override
            public void onException(Throwable e) {
                System.out.println("发送失败" + e.getMessage());
            }
        });
        System.out.println("我先执行");
        //关闭生产者
        System.in.read();
    }

    public void testOnewayProducer() throws Exception {
        DefaultMQProducer producer = new DefaultMQProducer("oneway-producer-group");
        producer.setNamesrvAddr("192.168.5.73:9876");
        producer.start();
        Message message = new Message("onewayTopic", "单向消息发送".getBytes(StandardCharsets.UTF_8));
        producer.sendOneway(message);
        producer.shutdown();
    }

    public void testMsProducer() throws Exception {
        DefaultMQProducer producer = new DefaultMQProducer("ms-producer-group");
        producer.setNamesrvAddr("192.168.5.73:9876");
        producer.start();
        Message message = new Message("msTopic", "延迟消息发送".getBytes(StandardCharsets.UTF_8));
        message.setDelayTimeLevel(3);
        producer.send(message);
        producer.shutdown();
    }

    public void testMsConsumer() throws Exception {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("orderly-consumer-group");
        consumer.setNamesrvAddr("192.168.5.73:9876");
        consumer.subscribe("orderlyTopic", "*");
        consumer.registerMessageListener((MessageListenerConcurrently) (msgs, context) -> {
            for (MessageExt msg : msgs) {
                byte[] body = msg.getBody();
                String s = new String(body, StandardCharsets.UTF_8);
                System.out.println("接收到消息：" + s);
            }
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        });
        consumer.start();
        System.in.read();
    }

    public void testBatchProducer() throws Exception {
        DefaultMQProducer producer = new DefaultMQProducer("batch-producer-group");
        producer.setNamesrvAddr("192.168.5.73:9876");
        producer.start();
        List<Message> list = Arrays.asList(new Message("batchTopic", "我是消息A".getBytes(StandardCharsets.UTF_8)), new Message("batchTopic", "我是消息B".getBytes(StandardCharsets.UTF_8)), new Message("batchTopic", "我是消息C".getBytes(StandardCharsets.UTF_8)));
        producer.send(list);
        producer.shutdown();
    }

    public void testOrderly() throws Exception {
        List<OrderModel> orderModels = Arrays.asList(new OrderModel("qwer", "1", "1"), new OrderModel("qwer", "1", "2"), new OrderModel("qwer", "1", "3"), new OrderModel("zxcv", "2", "1"), new OrderModel("zxcv", "2", "2"), new OrderModel("zxcv", "2", "3"));
        DefaultMQProducer producer = new DefaultMQProducer("orderly-producer-group");
        producer.setNamesrvAddr("192.168.5.73:9876");
        producer.start();
        orderModels.forEach(orderModel -> {
            Message message = new Message("orderlyTopic", orderModel.toString().getBytes(StandardCharsets.UTF_8));
            try {
                producer.send(message, (mqs, msg, arg) -> {
                    int i = arg.toString().hashCode();
                    int index = i % mqs.size();
                    return mqs.get(index);
                }, orderModel.getOrderSn());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        producer.shutdown();
    }

    public void testOrderlyConsumer() throws Exception {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("orderly-consumer-group");
        consumer.setNamesrvAddr("192.168.5.73:9876");
        consumer.subscribe("orderlyTopic", "*");
        consumer.registerMessageListener((MessageListenerOrderly) (msgs, context) -> {
            System.out.println("线程id:" + Thread.currentThread().getId());
            for (MessageExt msg : msgs) {
                System.out.println(new String(msg.getBody(), StandardCharsets.UTF_8));
            }
            return ConsumeOrderlyStatus.SUCCESS;
        });
        consumer.start();
        System.in.read();
    }

    public void testTapProducer() throws Exception {
        DefaultMQProducer producer = new DefaultMQProducer("tag-producer-group");
        producer.setNamesrvAddr("192.168.5.73:9876");
        producer.start();
        Message message1 = new Message("tagTopic", "vip1", "我是vip1的文章".getBytes(StandardCharsets.UTF_8));
        Message message2 = new Message("tagTopic", "vip2", "我是vip2的文章".getBytes(StandardCharsets.UTF_8));
        producer.send(message1);
        producer.send(message2);
        System.out.println("发送成功");
        producer.shutdown();
    }

    public void testTagConsumer1() throws Exception {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("tag-consumer-group-a");
        consumer.setNamesrvAddr("192.168.5.73:9876");
        consumer.subscribe("tagTopic", "vip1");
        consumer.registerMessageListener((MessageListenerConcurrently) (msgs, context) -> {
            for (MessageExt msg : msgs) {
                System.out.println("我是vip1的消费者，我收到消息：" + new String(msg.getBody(), StandardCharsets.UTF_8));
            }
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        });
        consumer.start();
        System.in.read();
    }

    public void testTagConsumer2() throws Exception {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("tag-consumer-group-b");
        consumer.setNamesrvAddr("192.168.5.73:9876");
        consumer.subscribe("tagTopic", "vip1 || vip2");
        consumer.registerMessageListener((MessageListenerConcurrently) (msgs, context) -> {
            for (MessageExt msg : msgs) {
                System.out.println("我是vip2的消费者，我收到消息：" + new String(msg.getBody(), StandardCharsets.UTF_8));
            }
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        });
        consumer.start();
        System.in.read();
    }

    public void testKeyProducer() throws Exception {
        DefaultMQProducer producer = new DefaultMQProducer("key-producer-group");
        producer.setNamesrvAddr("192.168.5.73:9876");
        producer.start();
        String uuid = UUID.randomUUID().toString();

        Message message = new Message("keyTopic", "vip1", uuid, "我是vip1的文章".getBytes(StandardCharsets.UTF_8));
        producer.send(message);
        System.out.println("发送成功");
        producer.shutdown();
    }

    public void testRetryProducer() throws Exception {
        DefaultMQProducer producer = new DefaultMQProducer("retry-producer-group");
        producer.setNamesrvAddr("192.168.5.73:9876");
        producer.start();
        Message message = new Message("retryTopic", "vip1", "我是vip1的文章".getBytes(StandardCharsets.UTF_8));
        producer.send(message);
        System.out.println("发送成功");
        producer.shutdown();
    }

    public void testRetryConsumer() throws Exception {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("retry-consumer-group");
        consumer.setNamesrvAddr("192.168.5.73:9876");
        consumer.subscribe("retryTopic", "*");
        //设置重试次数
        consumer.setMaxReconsumeTimes(2);
        consumer.registerMessageListener((MessageListenerConcurrently) (msgs, context) -> {
            System.out.println("线程id:" + Thread.currentThread().getId());
            for (MessageExt msg : msgs) {
                System.out.println(new String(msg.getBody(), StandardCharsets.UTF_8));
            }
            return ConsumeConcurrentlyStatus.RECONSUME_LATER;
        });
        consumer.start();
        System.in.read();
    }

    public void testRetryDeadConsumer() throws Exception {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("retry-dead-consumer-group");
        consumer.setNamesrvAddr("192.168.5.73:9876");
        //死信队列
        consumer.subscribe("%DLQ%retry-consumer-group", "*");
        consumer.registerMessageListener((MessageListenerConcurrently) (msgs, context) -> {
            System.out.println("线程id:" + Thread.currentThread().getId());
            for (MessageExt msg : msgs) {
                System.out.println(new String(msg.getBody(), StandardCharsets.UTF_8));
            }
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        });
        consumer.start();
        System.in.read();
    }

    public void testRetryConsumer2() throws Exception {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("retry-consumer-group");
        consumer.setNamesrvAddr("192.168.5.73:9876");
        consumer.subscribe("retryTopic", "*");
        consumer.registerMessageListener((MessageListenerConcurrently) (msgs, context) -> {
            System.out.println("线程id:" + Thread.currentThread().getId());
            for (MessageExt msg : msgs) {
                try {
                    int i = 1 / 0;
                } catch (Exception e) {
                    int reconsumeTimes = msg.getReconsumeTimes();
                    if (reconsumeTimes >= 3) {
                        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                    }
                    return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                }
                System.out.println(new String(msg.getBody(), StandardCharsets.UTF_8));
            }
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        });
        consumer.start();
        System.in.read();
    }
}
