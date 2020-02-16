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

    public static String buildParams(Object[] params) {
        if(ArrayUtils.isEmpty(params)) {
            return StringUtils.EMPTY;
        } else {
            StringBuilder sb =  new StringBuilder(512);
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
}
