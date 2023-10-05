package com.myz.rocketmq;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;

import java.nio.charset.StandardCharsets;
import java.util.List;

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
        consumer.subscribe("onewayTopic", "*");
        //设置一个监听器
        consumer.registerMessageListener((MessageListenerConcurrently) (list, context) -> {
            System.out.println("我是消费者");
            for (MessageExt messageExt : list) {
                String s = new String(messageExt.getBody(), StandardCharsets.UTF_8);
                System.out.println(">>>>>" + s);

            }
            System.out.println("消费上下文：" + context);

            /**
             * CONSUME_SUCCESS:消息会从mq出队
             * RECONSUME_LATER:（报错/null）失败，消息会重新回到队列，过一会儿重新投递出来
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
}
