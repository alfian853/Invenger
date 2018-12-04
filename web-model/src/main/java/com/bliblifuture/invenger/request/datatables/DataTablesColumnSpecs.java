package com.bliblifuture.invenger.request.datatables;

import lombok.Data;

import javax.servlet.http.HttpServletRequest;

@Data
public class DataTablesColumnSpecs {

    private int index;

    private String data;

    private String name;

    private boolean searchable;

    private boolean orderable;

    private String search;

    private boolean regex;

    private String sortDir;

    private boolean isSortable = false;


    public DataTablesColumnSpecs(HttpServletRequest request, int i) {
        this.setIndex(i);
        prepareColumnSpecs(request, i);
    }

    private void prepareColumnSpecs(HttpServletRequest request, int i) {

        this.data = request.getParameter("columns["+ i +"][data]");
        this.name = request.getParameter("columns["+ i +"][name]");
        this.orderable = Boolean.valueOf(request.getParameter("columns["+ i +"][orderable]"));
        this.regex = Boolean.valueOf(request.getParameter("columns["+ i +"][search][regex]"));
        this.search = request.getParameter("columns["+ i +"][search][value]");
        this.searchable = Boolean.valueOf(request.getParameter("columns["+ i +"][searchable]"));

        int sortableCol = Integer.parseInt(request.getParameter("order[0][column]"));
        String sortDir = request.getParameter("order[0][dir]");

        if(i == sortableCol) {
            this.sortDir = sortDir;
            this.isSortable = true;
        }
    }


}
