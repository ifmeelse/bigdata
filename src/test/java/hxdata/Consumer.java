/**
 * Project Name:hxdata
 * File Name:T.java
 * Package Name:hxdata
 * Date:2017年3月1日下午1:49:51
 * Copyright (c) 2017, hx2car.com All Rights Reserved.
 *
*/
/**
 * Project Name:hxdata
 * File Name:T.java
 * Package Name:hxdata
 * Date:2017年3月1日下午1:49:51
 * Copyright (c) 2017, geyang All Rights Reserved.
 *
 */

package hxdata;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

/**
 * ClassName:T 
 * Function: TODO ADD FUNCTION 
 * Reason:	 TODO ADD REASON
 * Date:     2017年3月1日 下午1:49:51 
 * @author   geyang
 * @version  
 * @since    JDK 1.6
 * @see 	 
 */
/**
 * date: 2017年3月1日 下午1:49:51 
 * @author geyang
 */
public class Consumer {
public static void main(String[] args) {
	
	String line="http://test.2schome.net/car/search.htm?more=l330200jyckbmg&carFlag=verify";
	if (line.contains("search.htm")&&line.contains("more=")) {
		 line=StringUtils.substringAfter(line, "more=");
//		area_code=area_code.substring(1, 7);
		if (line.contains("more=")) {
//			int indexstart = line.lastIndexOf("more=") + 5;
			String parame = line.substring(0, line.length());
			int indexend = parame.indexOf("&");
			if (parame.contains("/")) {
				int ind2 = parame.indexOf("/");
				if (ind2 < indexend) {
					indexend = ind2;
				}
			}
			String parametersmore = parame.substring(0, indexend);
			// System.out.println(parametersmore);
			List<String> volist = new ArrayList<String>();
			int t_i = 0;
			if (parametersmore != null && !"".equalsIgnoreCase(parametersmore)) {
				for (int i = 0; i < parametersmore.length(); i++) {
					String temp_i = String.valueOf(parametersmore.charAt(i));
					if (temp_i.matches("[a-zA-Z]*")) {
						if (i > 0) {
							volist.add(parametersmore.substring(t_i, i));
						}
						t_i = i;
					}
					if (i == parametersmore.length() - 1) {
						volist.add(parametersmore.substring(t_i, parametersmore.length()));
					}
				}

				for (int i = 0; i < volist.size(); i++) {
					String temp_i = String.valueOf(volist.get(i).charAt(0));
					 if (temp_i != null && temp_i.equalsIgnoreCase("l")
							&& !volist.get(i).equalsIgnoreCase("l")) {
						try {
//                         areacode=volist.get(i).replace("l", "");
							
						} catch (Exception e) {
							// TODO Auto-generated catch
							// block
							e.printStackTrace();
						}
					} 
				}
			}
		}
		
	}
}
}

