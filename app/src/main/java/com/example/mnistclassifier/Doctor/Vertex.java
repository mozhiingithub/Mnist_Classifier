package com.example.mnistclassifier.Doctor;

import java.util.List;

public class Vertex {//节点类
    private List<Edge> edgeList;//节点所有出度边的列表


    private String response;//当节点没有合适的边进行选择时，给予的提示

    public Vertex(String response) {
        this.response = response;
    }

    public void setEdgeList(List<Edge> edgeList) {
        this.edgeList = edgeList;
    }

    public Edge selectEdge(String content) {
        for (int i = 0; i < edgeList.size(); i++) {
            Edge edge = edgeList.get(i);
            if (edge.match(content)) return edge;
        }
        return null;
    }

    public String getResponse() {
        return response;
    }
}
