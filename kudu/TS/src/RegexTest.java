
public class RegexTest {

	public static void main(String args[]) {

		String regex = "^[0-9]{1,9}-[0-9]{1,14}-[0-9]{1,9}-www-bai-du\\.txt\\b$";
		String regex1 = "^[0-9]{1,15}-[0-9]{1,15}-[0-9]{1,15}-[0-9]{1,15}-WA_SOURCE_FJ_1001-[0-9]{1,9}\\.bcp\\b$";
		
		String regex2 = "^.*WA_SOURCE_0005.*\\.bcp\\b$";

		String temp = "111-2322555443-2323211-www-bai-du.txt";
		String temp2 = "145-310115-1547517641-32956-WA_SOURCE_FJ_1001-0.bcp";
		String temp3 = "120-310115-1547823187-10488-WA_SOURCE_0005-0.bcp";
		System.err.println(temp.matches(regex));

		System.err.println(temp3.matches(regex2));
		
		String aaa= "E8-65-D4-30-82-C8			1548062652	-72	-1			00-E0-0F-2E-1E-F0	6		10.80	10.80	31011521001210	55740434400E00F2E1EF0	121.676208	31.199173\r\n" + 
				"";
		
		
/*
		String tt = "所	颠	三	倒	四大苏打	dff		dfdfd				1";
		String rr = "\\t";
		System.err.println(tt.matches(rr));

		String[] ss = tt.split(rr);
		for (int i = 0; i < ss.length; i++) {
			System.err.println(i + 1 + "--" + ss[i]);
			
			if(ss[i]!=null && !"".equals(ss[i])) {
				//
			}
			
		}
		
		*/
		
		
		
		
		
		
		
		

	}
}
