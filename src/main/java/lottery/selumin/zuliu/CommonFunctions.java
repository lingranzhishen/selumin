package lottery.selumin.zuliu;

import java.util.List;


public class CommonFunctions {


	public static boolean isAllEvenOrOdd(int a,int b,int c){
		return a%2==b%2&&a%2==c%2;
	}

	public static boolean isAllBigOrSmall(int a,int b,int c){
		return a/5==b/5&&a/5==c/5;
	}

	public static boolean isCombinationThree(int a,int b,int c){
		return a==b||b==c||a==c;
	}

	public static String getCombinationSingleSix(){
		StringBuilder stringBuilder=new StringBuilder();
		for(int a=0; a<10; a++)
			for(int b=0; b<10; b++)
				for(int c=0; c<10; c++){
					if(isAllEvenOrOdd(a,b,c)||isAllBigOrSmall(a,b,c)||isCombinationThree(a,b,c)){

					}else{
						stringBuilder.append(a).append(b).append(c).append("\n");
					}
				}
		return stringBuilder.toString();
	}

	public static void main(String [] args){
		System.out.print(getCombinationSingleSix());
	}
}
