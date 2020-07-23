package com.example.mnistclassifier;

import java.io.IOException;
import java.util.List;

public class Doctor {
    private enum Status {
        ASK_POSITION,
        POSITION_TOP,
        ASK_TOP,
        POSITION_BASE,
        ASK_FALL;
    }

    private Status status;
    private Classifier classifier;

    private MsgManager msgManager;

    public Doctor(MainActivity mainActivity) {
        this.msgManager = mainActivity.getMsgManager();
        status = Status.ASK_POSITION;
        try {
            classifier = new Classifier(mainActivity);
        } catch (IOException e) {
            e.printStackTrace();
        }
        msgManager.addTextMsg("欢迎使用柑橘叶片疾病诊断系统。", Msg.TYPE_RECEIVE); // 信息列表里添加欢迎语
        msgManager.addTextMsg("请问您要诊断的叶片位于顶梢还是基枝？", Msg.TYPE_RECEIVE);
    }

    public void diagnose() {//对用户提供的信息进行诊断
        Msg msg = msgManager.getLastMsg();//获取最新的信息
        if (Msg.TYPE_RECEIVE == msg.getType()) return;//最新信息为医生发送的内容
        switch (msg.getContent_type()) {//按信息类型，分情况分析
            case Msg.TYPE_TEXT://文本信息
                switch (status) {
                    case ASK_POSITION://系统询问异常叶片所处位置
                        switch (msg.getText_content()) {
                            case "顶梢"://用户回答，叶片位于顶梢
                                msgManager.addTextMsg("请上传顶梢叶片的图片。", Msg.TYPE_RECEIVE);
                                status = Status.POSITION_TOP;//等待识别用户提交顶梢叶片图片
                                break;
                            case "基枝"://用户回答，叶片位于基枝
                                msgManager.addTextMsg("请问顶梢部位的叶片是否有异常情况？（是/否）", Msg.TYPE_RECEIVE);
                                status = Status.ASK_TOP;//等待用户回答顶梢部位的情况
                                break;
                            default://用户没有正确回答异常叶片所处位置
                                msgManager.addTextMsg("请问您要诊断的叶片位于顶梢还是基枝？（顶梢/基枝）", Msg.TYPE_RECEIVE);
                        }
                        break;
                    case POSITION_TOP://用户在系统等待接收图片时，依旧发送文字信息，此时应提醒用户上传图片
                        msgManager.addTextMsg("请上传顶梢叶片的图片。", Msg.TYPE_RECEIVE);
                        break;
                    case ASK_TOP://系统询问用户顶梢部位是否异常
                        switch (msg.getText_content()) {
                            case "是"://用户在先表示基枝异常的情况下，又表示顶梢有异常，则优先诊断顶梢叶片
                                msgManager.addTextMsg("请上传顶梢叶片的图片。", Msg.TYPE_RECEIVE);
                                status = Status.POSITION_TOP;//等待识别用户提交顶梢叶片图片
                                break;
                            case "否"://用户表示基枝异常，顶梢正常，则诊断基枝叶片
                                msgManager.addTextMsg("请上传基枝叶片的图片。", Msg.TYPE_RECEIVE);
                                status = Status.POSITION_BASE;//等待识别用户提交基枝叶片图片
                                break;
                            default://用户没有正确回答顶梢部位的情况
                                msgManager.addTextMsg("请准确回答顶梢是否有异常。（是/否）", Msg.TYPE_RECEIVE);
                        }
                        break;
                    case ASK_FALL://系统询问柑橘是否有大量落叶
                        switch (msg.getText_content()) {
                            case "是":
                                msgManager.addTextMsg("您的柑橘可能患黄龙病，病情处于中后期。", Msg.TYPE_RECEIVE);
                                status = Status.ASK_POSITION;
                                break;
                            case "否":
                                msgManager.addTextMsg("您的柑橘可能有生理性缺素。", Msg.TYPE_RECEIVE);
                                status = Status.ASK_POSITION;
                                break;
                            default://用户没有正确回答落叶的情况
                                msgManager.addTextMsg("请准确回答柑橘是否有大量落叶。（是/否）", Msg.TYPE_RECEIVE);
                        }
                        break;
                }
                break;
            case Msg.TYPE_IMG:
                // 对图片进行分类
                int classify_res = classifier.classify(msg.getImg_content());
                switch (status) {//根据叶片来自部位的不同，分情况分析
                    case POSITION_TOP://顶梢
                        switch (classify_res) {
                            case 0:
                                msgManager.addTextMsg("您的顶梢叶片非常健康。", Msg.TYPE_RECEIVE);
                                status = Status.ASK_POSITION;//诊断结束，节点状态回到原始
                                break;
                            case 1:
                                msgManager.addTextMsg("您的顶梢叶片呈均匀型黄化特征，您的柑橘可能患黄龙病。", Msg.TYPE_RECEIVE);
                                status = Status.ASK_POSITION;//诊断结束，节点状态回到原始
                                break;
                            case 2:
                                msgManager.addTextMsg("您的顶梢叶片呈斑驳型黄化特征，您的柑橘可能患黄龙病。", Msg.TYPE_RECEIVE);
                                status = Status.ASK_POSITION;//诊断结束，节点状态回到原始
                                break;
                            case 3:
                                msgManager.addTextMsg("您的顶梢叶片有缺锌症状。请问柑橘是否有大量落叶？", Msg.TYPE_RECEIVE);
                                status = Status.ASK_FALL;//切换至询问落叶状态
                                break;
                            case 4:
                                msgManager.addTextMsg("您的顶梢叶片有缺锰症状。请问柑橘是否有大量落叶？", Msg.TYPE_RECEIVE);
                                status = Status.ASK_FALL;//切换至询问落叶状态
                                break;
                            case 5:
                                msgManager.addTextMsg("您的顶梢叶片有缺镁症状。请问柑橘是否有大量落叶？", Msg.TYPE_RECEIVE);
                                status = Status.ASK_FALL;//切换至询问落叶状态
                                break;
                            default:
                                msgManager.addTextMsg("您的顶梢叶片有一定程度的黄化，但病因非黄龙病或生理性缺素。", Msg.TYPE_RECEIVE);
                                status = Status.ASK_POSITION;//诊断结束，节点状态回到原始
                        }
                        break;
                    case POSITION_BASE://基枝
                        switch (classify_res) {
                            case 0:
                                msgManager.addTextMsg("您的基枝叶片非常健康。", Msg.TYPE_RECEIVE);
                                break;
                            case 3:
                                msgManager.addTextMsg("您的柑橘可能生理性缺锌。", Msg.TYPE_RECEIVE);
                                break;
                            case 4:
                                msgManager.addTextMsg("您的柑橘可能生理性缺锰。", Msg.TYPE_RECEIVE);
                                break;
                            case 5:
                                msgManager.addTextMsg("您的柑橘可能生理性缺镁。", Msg.TYPE_RECEIVE);
                                break;
                            default:
                                msgManager.addTextMsg("您的基枝叶片有一定程度的黄化，但病因非黄龙病或生理性缺素。", Msg.TYPE_RECEIVE);
                        }
                        status = Status.ASK_POSITION;//诊断结束，节点状态回到原始
                        break;
                }
                break;

        }

    }

    public boolean waitForImg() {
        return status == Status.POSITION_TOP || status == Status.POSITION_BASE;
    }

    private static class Vertex{
        private List<Edge> outEdges;
    }

    private static class Edge{
        
    }
}
