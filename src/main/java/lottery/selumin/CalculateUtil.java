package lottery.selumin;

import org.apache.commons.lang3.StringUtils;

public abstract class CalculateUtil {
	public static int getSequenceIntValue(String sequenceNo) {
		if (StringUtils.isNoneEmpty(sequenceNo)) {
			try {
				return Integer.valueOf(sequenceNo.split("-")[1]);
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		return 0;
	}
}
