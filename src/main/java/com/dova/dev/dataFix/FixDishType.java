package com.dova.dev.dataFix;

import com.dova.dev.common.JSON;
import com.dova.dev.jdbc.ConnectionTool;
import com.google.common.base.Strings;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuzhendong on 16/7/12.
 */
public class FixDishType {



    public static final String dishIds = "4832,4839";

    public static void fix() throws SQLException,IOException{
        Connection connection = ConnectionTool.createConnection("jdbc:mysql://101.200.143.50:3306/jskz_2.0?characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&tinyInt1isBit=false",
                "root","JSKZ-fc4b679d0%");
        if(connection == null){
            System.out.println("建立连接失败");
            return;
        }

        connection.setAutoCommit(true);
        String update = "update food_kitchen_dish set `type` = ? where id = ?";
        PreparedStatement updatePs = connection.prepareStatement(update);

        BufferedReader br = new BufferedReader(new FileReader(new File("/tmp/dish_wash.csv")));
        String line = null;
        while ((line = br.readLine()) != null){
            if(line.length() == 0) continue;
            String[] items = line.split(",");
            if(items.length < 2){
                throw new RuntimeException("items len < " + items.length);
            }
            Dish dish = new Dish(Long.valueOf(items[0].trim()), DishTypeUtil.parse(items[1].trim()));
            System.out.println(String.format("dish:%d type:%d",dish.dishId, dish.dishType.getValue()));
            updatePs.clearParameters();
            updatePs.setInt(1, dish.dishType.getValue());
            updatePs.setLong(2, dish.dishId);
            int res = updatePs.executeUpdate();
            if(res !=1){
                System.out.println(dish.dishId + ":update fail ");
            }else {
                System.out.println(dish.dishId + ":update succ ");
            }
        }
     }



    public static void main(String[] args)throws Exception{
        fix();
    }
}
