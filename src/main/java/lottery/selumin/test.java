package lottery.selumin;

public class test {

	public static void main(String []args){
		double initialMoney=28.8;
		double unitCost=0.24;
		double cost=0.24;
		double awardRatio=0.336/0.24;
		double aimMoney=initialMoney;
		for(int i=0; i<10; i++){
			aimMoney+=cost*(1+awardRatio);
			if((aimMoney-initialMoney)/unitCost>1){
				cost=unitCost*((aimMoney-initialMoney)/unitCost);
			}
		}
		System.out.println("aimMoney:"+aimMoney);
		System.out.println("winningPercent:"+(aimMoney-initialMoney)/initialMoney);
	}
}
