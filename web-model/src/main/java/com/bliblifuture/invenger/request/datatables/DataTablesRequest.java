package com.bliblifuture.invenger.request.datatables;

import lombok.Data;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Data
public class DataTablesRequest {

    private String uniqueId;

    private String draw;

    private Integer start;

    private Integer length;

    private String search;

    private boolean regex;

    private List<DataTablesColumnSpecs> columns;

    private DataTablesColumnSpecs order;

    private boolean isGlobalSearch;

    public DataTablesRequest(HttpServletRequest request) {
        prepareDataTableRequest(request);
    }


    public static boolean isObjectEmpty(Object object) {
        if(object == null) return true;
        else if(object instanceof String) {
            return ((String) object).trim().length() == 0;
        }
        return false;
    }

    private void prepareDataTableRequest(HttpServletRequest request) {

        Enumeration<String> parameterNames = request.getParameterNames();

        if(parameterNames.hasMoreElements()) {

            this.setStart(Integer.parseInt(request.getParameter("start")));
            this.setLength(Integer.parseInt(request.getParameter("length")));
            this.setUniqueId(request.getParameter("_"));
            this.setDraw(request.getParameter("draw"));

            this.setSearch(request.getParameter("search[value]"));
            this.setRegex(Boolean.valueOf(request.getParameter("search[regex]")));

            int sortableCol = Integer.parseInt(request.getParameter("order[0][column]"));

            List<DataTablesColumnSpecs> columns = new ArrayList<>();

            if(!isObjectEmpty(this.getSearch()) ) {
                this.setGlobalSearch(true);
            }

            int maxParamsToCheck = getNumberOfColumns(request);

            for(int i=0; i < maxParamsToCheck; i++) {
                if(null != request.getParameter("columns["+ i +"][data]")
                        && !"null".equalsIgnoreCase(request.getParameter("columns["+ i +"][data]"))
                        && !isObjectEmpty(request.getParameter("columns["+ i +"][data]"))) {
                    DataTablesColumnSpecs colSpec = new DataTablesColumnSpecs(request, i);
                    if(i == sortableCol) {
                        this.setOrder(colSpec);
                    }
                    columns.add(colSpec);

                    if(!isObjectEmpty(colSpec.getSearch())) {
                        this.setGlobalSearch(false);
                    }
                }
            }

            if(!isObjectEmpty(columns)) {
                this.setColumns(columns);
            }
        }
    }

    private int getNumberOfColumns(HttpServletRequest request) {
        Pattern p = Pattern.compile("columns\\[[0-9]+\\]\\[data\\]");
        @SuppressWarnings("rawtypes")
        Enumeration params = request.getParameterNames();
        List<String> lstOfParams = new ArrayList<>();
        while(params.hasMoreElements()){
            String paramName = (String)params.nextElement();
            Matcher m = p.matcher(paramName);
            if(m.matches())	{
                lstOfParams.add(paramName);
            }
        }
        return lstOfParams.size();
    }


}
