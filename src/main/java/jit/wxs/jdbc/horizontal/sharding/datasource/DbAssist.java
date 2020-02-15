package jit.wxs.jdbc.horizontal.sharding.datasource;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

import static java.util.regex.Pattern.*;

/**
 * @author jitwxs
 * @date 2020年02月15日 14:23
 */
@Slf4j
public class DbAssist {
    public static boolean checkSql(String sql) {
        sql = sql.toLowerCase();
        if(sql.contains("insert ") && sql.contains("into ")) {
            return true;
        }
        if(sql.contains("select ")) {
            return sql.contains("where ") || sql.contains("limit ");
        }
        if(sql.contains("update ") || sql.contains(" set ") || sql.contains("delete ") || sql.contains(" from ")) {
            return sql.contains("where ");
        }
        return false;
    }

    public static List<String> getTableName(String sql) {
        if(StringUtils.isEmpty(sql)) {
            return Collections.emptyList();
        }

        List<String> tableList = new ArrayList<>();
        String sqlType = sql.trim().substring(0, sql.trim().indexOf(" "));


        if ("select".equals(sqlType.toLowerCase()) || "delete".equals(sqlType.toLowerCase())) {
            tableList.addAll(getSelectOrDelete(sql));
        } else if ("insert".equals(sqlType.toLowerCase()) || "replace".equals(sqlType.toLowerCase())) {
            String[] temp = sql.trim().split("into");
            if (ArrayUtils.isNotEmpty(temp)) {
                String tmp = temp[1].trim();
                int length;
                length = tmp.indexOf("values");
                if (length == -1) {
                    length = tmp.indexOf("VALUES");
                    if (length == -1) {
                        length = tmp.indexOf("value");
                        if (length == -1) {
                            length = tmp.indexOf("VALUE");
                            if (length == -1){
                                length = tmp.indexOf("set");
                                if (length == -1) {
                                    length = tmp.indexOf("SET");
                                }
                                if(length == -1) {
                                    length = tmp.indexOf("select");
                                }
                                if (length == -1) {
                                    length = tmp.indexOf("SELECT");
                                }
                            }
                        }
                    }
                }
                if (length <= 0) {
                    return null;
                }

                tmp = tmp.substring(0, length);
                tmp = splitStr(tmp, '(')[0].trim();
                if (tmp.length() > 0) {
                    tableList.add(tmp);
                }
            }
        } else if ("update".equals(sqlType.toLowerCase())) {
            String strSql = sql.trim();
            String[] temp = sql.trim().split(" ");
            int count = 0;
            for (String s : temp) {
                String tmp = s.trim();
                if (tmp.length() > 0 && !"from".equals(tmp.toLowerCase())) {
                    count++;
                }
                if (count == 2) {
                    tableList.add(tmp);
                    strSql = sql.substring(sql.indexOf(tmp) + tmp.length());
                    break;
                }
            }
            if (strSql.length() > 0) {
                List<String> tableName = getSelectOrDelete(strSql);
                if (tableName.size() > 0) {
                    tableList.addAll(tableName);
                }
            }
        } else {
            List<String> tableName = getSelectOrDelete(sql);
            if (tableName.size() > 0) {
                tableList.addAll(tableName);
            }
        }

        return tableList;
    }

    public static String buildParams(Object[] params) {
        if(ArrayUtils.isEmpty(params)) {
            return StringUtils.EMPTY;
        } else {
            StringBuilder sb =  new StringBuilder("512");
            for (Object param : params) {
                sb.append(param).append(", ");
            }
            return sb.toString();
        }
    }

    public static List<Map<String, Object>> getResultMap(ResultSet resultSet) throws SQLException {
        if(resultSet == null) {
            return Collections.emptyList();
        }

        List<Map<String, Object>> result = new ArrayList<>();
        int columnCount = resultSet.getMetaData().getColumnCount();
        while (resultSet.next()) {
            Map<String, Object> resultRow = new HashMap<>();
            for (int i = 1; i <= columnCount; i++) {
                resultRow.put(resultSet.getMetaData().getColumnLabel(i), resultSet.getObject(i));
            }
            result.add(resultRow);
        }
        return result;
    }

    public static <T> List<T> convert(List<Map<String, Object>> mapList, Class<T> clazz) {
        return mapList.stream().map(e -> convert(e, clazz)).collect(Collectors.toList());
    }

    public static <T> T convert(Map<String, Object> map, Class<T> clazz) {
        if(MapUtils.isEmpty(map)) {
            return null;
        }

        Map<String, Object> result = new HashMap<>();
        map.forEach((k,v) -> result.put(underlineToCamel2(k), v));
        map.clear();

        return JSONArray.parseObject(JSON.toJSONString(result), clazz);
    }

    public static String underlineToCamel2(String param){
        if(StringUtils.isBlank(param)) {
            return StringUtils.EMPTY;
        }
        param = param.toLowerCase();
        StringBuilder sb=new StringBuilder(param);
        Matcher mc= compile("_").matcher(param);
        int i = 0;
        while (mc.find()){
            int position=mc.end()-(i++);
            sb.replace(position-1,position+1,sb.substring(position,position+1).toUpperCase());
        }
        return sb.toString();
    }

    /**
     * @param source 源字符串
     * @param oldString 老的字符串
     * @param newString 新的字符串
     * @return 替换后的字符串
     */
    private static String replaceStr(String source, String oldString, String newString) {
        StringBuilder sb = new StringBuilder();
        int lengthOfSource = source.length(), lengthOfOld = oldString.length();
        // 开始搜索位置
        int posStart = 0;
        // 搜索到老字符串的位置
        int pos;

        String sourceLower = source.toLowerCase();
        String oldLower = oldString.toLowerCase();
        while ((pos = sourceLower.indexOf(oldLower, posStart)) >= 0) {
            sb.append(source, posStart, pos);
            sb.append(newString);
            posStart = pos + lengthOfOld;
        }
        if (posStart < lengthOfSource) {
            sb.append(source.substring(posStart));
        }
        return sb.toString();
    }

    private static String[] splitStr(String str, char c){
        str += c;
        int n=0;
        for(int i=0;i<str.length();i++){
            if(str.charAt(i)==c) {
                n++;
            }
        }
        String[] out = new String[n];
        for(int i = 0; i < n; i++){
            int index = str.indexOf(c);
            out[i] = str.substring(0, index);
            str = str.substring(index + 1);
        }
        return out;
    }

    private static List<String> getSelectOrDelete(String sql) {
        List<String> tableList = new ArrayList<>();
        sql = replaceStr(sql, "FROM", "from");
        String[] temp = sql.trim().split("from");
        for (int i = 0; i < temp.length; i++) {
            if (i != 0) {
                String tmp = temp[i].trim();
                if(tmp.length() == 0){
                    continue;
                }
                int length = tmp.indexOf(" ");
                if (length != -1) {
                    tmp = tmp.substring(0, tmp.indexOf(" "));
                    length = tmp.indexOf(")");
                    if (length != -1) {
                        tmp = tmp.substring(0, tmp.indexOf(")"));
                    }
                } else {
                    length = tmp.indexOf(")");
                    if (length != -1) {
                        tmp = tmp.substring(0, tmp.indexOf(")"));
                    }
                }
                // 开始到第一个空格之间如果不是select的话，一定是表名
                if (!"(".equals(tmp.charAt(0) + "")) {
                    tableList.add(tmp);
                }
            }
        }
        // join
        temp = sql.split("join");
        for (int i = 0; i < temp.length; i++) {
            if (i != 0) {
                String tmp = temp[i].trim();
                int length = tmp.indexOf(" ");
                if (length == -1) {
                    continue;
                }
                tmp = tmp.substring(0, length);
                if (!"(".equals(tmp.charAt(0) + "")) {
                    tableList.add(tmp);
                }
            }
        }
        return tableList;
    }
}
