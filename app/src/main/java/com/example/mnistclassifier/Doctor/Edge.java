package com.example.mnistclassifier.Doctor;

public class Edge {//边类
    private Vertex vertex;//边指向节点
    private String matchStr;//边匹配条件字符串
    private String response;//选择该边的回复

    public Edge(Vertex vertex, String matchStr, String response) {
        this.vertex = vertex;
        this.matchStr = matchStr;
        this.response = response;
    }

    public Vertex getVertex() {
        return vertex;
    }

    public String getResponse() {
        return response;
    }

    public boolean match(String content) {//判断用户提供的内容以本边的匹配条件是否吻合
        return content.equals(matchStr);
    }
}
