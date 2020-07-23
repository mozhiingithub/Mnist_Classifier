package com.example.mnistclassifier.Doctor;

import java.util.Arrays;

public class Doctor {//医生类

    private Vertex current_vertex;//医生当前所处流程节点处

    private Vertex ask_pos;//询问叶片所处位置节点
    private Edge ask_pos_to_top;//叶片位于顶梢
    private Edge ask_pos_to_base;//叶片位于基枝

    private Vertex ask_top;//初选基枝，询问顶梢是否异常
    private Edge top_abnormal;//初选基枝，且顶梢异常
    private Edge top_normal;//初选基枝，且顶梢无异常

    private Vertex top_cls;//顶梢图片识别节点
    private Edge top_healthy;//顶梢健康
    private Edge top_avg;//顶梢均匀型黄化
    private Edge top_spot;//顶梢斑驳型黄化
    private Edge top_zn;//顶梢缺锌
    private Edge top_mn;//顶梢缺锰
    private Edge top_mg;//顶梢缺镁
    private Edge top_unknown;//顶梢不明症状

    private Vertex base_cls;//基枝图片识别节点
    private Edge base_healthy;//基枝健康
    private Edge base_avg;//基枝均匀型黄化
    private Edge base_spot;//基枝斑驳型黄化
    private Edge base_zn;//基枝缺锌
    private Edge base_mn;//基枝缺锰
    private Edge base_mg;//基枝缺镁
    private Edge base_unknown;//基枝不明症状

    private Vertex ask_fall;//顶梢缺素情况下，询问是否有大量落叶
    private Edge has_fall;//有大量落叶
    private Edge no_fall;//没有落叶


    public Doctor() {
        //初始化节点
        ask_pos = new Vertex("请回答您所要判断的叶片所在位置（顶梢/基枝）");
        ask_top = new Vertex("请准确回复顶梢是否异常。（是/否）");
        top_cls = new Vertex("顶梢识别过程有误或未上传顶梢叶片图片。");
        base_cls = new Vertex("基枝识别过程有误或未上传基枝叶片图片。。");
        ask_fall = new Vertex("请准确回答柑橘是否有大量落叶？（是/否）");

        //初始化边
        ask_pos_to_top = new Edge(top_cls, "顶梢", "请上传顶梢叶片的图片。");
        ask_pos_to_base = new Edge(ask_top, "基枝", "请问顶梢是否异常？");

        top_abnormal = new Edge(top_cls, "是", "请上传顶梢叶片的图片。");
        top_normal = new Edge(base_cls, "否", "请上传基枝叶片的图片");

        top_healthy = new Edge(ask_pos, "0", "您的顶梢叶片非常健康。");
        top_avg = new Edge(ask_pos, "1", "您的顶梢叶片呈均匀型黄化，属黄龙病症状。");
        top_spot = new Edge(ask_pos, "2", "您的顶梢叶片呈斑驳型黄化，属黄龙病症状。");
        top_zn = new Edge(ask_fall, "3", "您的顶梢叶片有缺锌症状。请问柑橘是否有大量落叶？");
        top_mn = new Edge(ask_fall, "4", "您的顶梢叶片有缺锰症状。请问柑橘是否有大量落叶？");
        top_mg = new Edge(ask_fall, "5", "您的顶梢叶片有缺镁症状。请问柑橘是否有大量落叶？");
        top_unknown = new Edge(ask_pos, "other", "您的顶梢叶片症状原因不明。");

        base_healthy = new Edge(ask_pos, "0", "您的基枝叶片非常健康。");
        base_avg = new Edge(ask_pos, "1", "您的基枝叶片呈均匀型黄化，但患黄龙病的可能性较低。");
        base_spot = new Edge(ask_pos, "2", "您的基枝叶片呈斑驳型黄化，但患黄龙病的可能性较低。");
        base_zn = new Edge(ask_pos, "3", "您的基枝叶片有缺锌症状。属生理性缺素症。");
        base_mn = new Edge(ask_pos, "4", "您的基枝叶片有缺锰症状。属生理性缺素症。");
        base_mg = new Edge(ask_pos, "5", "您的基枝叶片有缺镁症状。属生理性缺素症。");
        base_unknown = new Edge(ask_pos, "other", "您的基枝叶片症状原因不明。");

        has_fall = new Edge(ask_pos, "是", "您的柑橘可能患黄龙病，且病情处于中后期。");
        no_fall = new Edge(ask_pos, "否", "您的柑橘有生理性缺素症。");

        //将边加入到节点当中
        ask_pos.setEdgeList(Arrays.asList(ask_pos_to_top, ask_pos_to_base));
        ask_top.setEdgeList(Arrays.asList(top_abnormal, top_normal));
        top_cls.setEdgeList(Arrays.asList(
                top_healthy,
                top_avg,
                top_spot,
                top_zn,
                top_mn,
                top_mg,
                top_unknown
        ));
        base_cls.setEdgeList(Arrays.asList(
                base_healthy,
                base_avg,
                base_spot,
                base_zn,
                base_mn,
                base_mg,
                base_unknown
        ));
        ask_fall.setEdgeList(Arrays.asList(has_fall, no_fall));

        //设置初始节点
        current_vertex = ask_pos;
    }

    public boolean waitForImg() {//判断医生当前是否处于等待图片信息的状态
        return top_cls == current_vertex || base_cls == current_vertex;
    }

    public String response(String content) {//医生根据用户的描述内容作相应答复
        Edge edge = current_vertex.selectEdge(content);//从当前节点，根据用户内容，找到相应去向
        if (null == edge) return current_vertex.getResponse();//当前节点没有一个去向与用户描述内容吻合
        current_vertex = edge.getVertex();//找到相应情况，将当前节点切换到新的节点
        return edge.getResponse();//返回该去向的对应答复
    }
}
