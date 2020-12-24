package cn.ivdone.blog.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class RandomUtil {
    public static List getRandomList(List list, int num){
        if (list.size() <= num){
            return list ;
        }
        List randomList = new ArrayList() ;
        // 用于打乱索引
        List<Integer> oList = new ArrayList();
        for (int i=0; i<list.size(); i++){
            oList.add(i) ;
        }
        // 调用静态方法打乱索引
        Collections.shuffle(oList);
        // 从原数据中获取打乱的索引
        for (int i:oList.subList(0, num)){
            randomList.add(list.get(i)) ;
        }
        // 返回数据
        return randomList ;
    }
}
