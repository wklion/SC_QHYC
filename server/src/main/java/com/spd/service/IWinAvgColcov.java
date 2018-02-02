package com.spd.service;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public interface IWinAvgColcov {

	public List<LinkedHashMap> queryWinAvg2MinByTimeRange (HashMap paramMap); 

	public List<LinkedHashMap> queryCloCovByTimeRange (HashMap paramMap); 

}
