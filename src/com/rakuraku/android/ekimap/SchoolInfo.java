package com.rakuraku.android.ekimap;

/*//最寄駅情報クラス
public class SchoolInfo {
	public String name;		// 最寄駅名
	public String prev;		// 前の駅名 （始発駅の場合は null）
	public String next;		// 次の駅名 （終着駅の場合は null）
	public Double	x;		// 最寄駅の経度 （世界測地系）
	public Double	y;		// 最寄駅の緯度 （世界測地系）
	public int	distance;	// 指定の場所から最寄駅までの距離 （精度は 10 ｍ）
	public String	line;	// 最寄駅の存在する路線名
}*/

//最寄駅情報クラス
public class SchoolInfo {
	public String school_name;// スクール名
	public Double	lat;// 経度（測地系は検索時に指定したもの）
	public Double	lng;// 緯度（測地系は検索時に指定したもの）
	public Double	lat_center;// 地図表示する際の中心経度（測地系は検索時に指定したもの）
	public Double	lng_center;// 地図表示する際の中心緯度（測地系は検索時に指定したもの）
	public int	range;	// 指定の場所から最寄駅までの距離 （精度は 10 ｍ）
	public String	name;// 講座名称
	public String	content;//内容

	/*ダミー*/
	public String prev;		// 前の駅名 （始発駅の場合は null）
	public String next;		// 次の駅名 （終着駅の場合は null）
	public int	distance;	// 指定の場所から最寄駅までの距離 （精度は 10 ｍ）
	public String	line;	// 最寄駅の存在する路線名
}

