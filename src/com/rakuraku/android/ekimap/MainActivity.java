package com.rakuraku.android.ekimap;

import java.util.HashMap;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Marker;
import com.rakuraku.android.util.HttpAsyncLoader;

import android.os.Bundle;
import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Loader;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.widget.Toast;

public class MainActivity extends Activity implements LoaderCallbacks<String> {

	// �}�b�v�I�u�W�F�N�g�i1�j
	private GoogleMap googleMap = null;

	// �}�[�J�[�Ɖw����HashMap
	private HashMap<Marker, EkiInfo> ekiMarkerMap;

	// �n�}�̒��S�ʒu
	private CameraPosition centerPosition = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		ekiMarkerMap = new HashMap<Marker, EkiInfo>();

		// MapFragment���擾����(2)
		MapFragment mapFragment = (MapFragment) getFragmentManager()
				.findFragmentById(R.id.map);

		try {
			// �}�b�v�I�u�W�F�N�g���擾����i3�j
			googleMap = mapFragment.getMap();

			// Activity�����߂Đ������ꂽ�Ƃ��i4�j
			if (savedInstanceState == null) {
				// �t���O�����g��ۑ�����(5)
				mapFragment.setRetainInstance(true);
				// �n�}�̏����ݒ���s���i6�j
				mapInit();

				// InfoWindow�̃N���b�N���X�i�[�ǉ�
				googleMap.setOnInfoWindowClickListener(

						new OnInfoWindowClickListener() {

							@Override
							public void onInfoWindowClick(Marker marker) {

								// �w���̎��o��
								EkiInfo e = ekiMarkerMap.get(marker);

								Toast ts = Toast.makeText(getBaseContext(),
										e.name + "(" + e.distance + "m)\n"
										+ "�O�̉w:" + e.prev + "\n���̉w:" + e.next + "\n"
										+ e.line, Toast.LENGTH_LONG);
								ts.setGravity(Gravity.TOP, 0, 200);
								ts.show();

							}
						}
				);

				googleMap.setOnCameraChangeListener(
						new OnCameraChangeListener() {
							@Override
							public void onCameraChange(CameraPosition cameraPosition) {
								if (centerPosition !=null ) {
									if ( 0.5 < calcDistance(centerPosition, cameraPosition) ) {
										execMoyori();
									}
									Log.d(getClass().getName(), Double.toString(calcDistance(centerPosition, cameraPosition)));

									centerPosition = cameraPosition;
								}
							}
						}
				);
			}
		}
		// GoogleMap���g�p�ł��Ȃ��Ƃ�
		catch (Exception e) {
		}
	}

	// 2�_�Ԃ̋��������߂�(km)
	private double calcDistance(CameraPosition a, CameraPosition b) {

		double lata = Math.toRadians(a.target.latitude);
		double lnga = Math.toRadians(a.target.longitude);

		double latb = Math.toRadians(b.target.latitude);
		double lngb = Math.toRadians(b.target.longitude);

		double r = 6378.137; // �ԓ����a

		return r * Math.acos(Math.sin(lata) * Math.sin(latb) + Math.cos(lata) * Math.cos(latb) * Math.cos(lngb - lnga));
	}

	// �n�}�̏����ݒ�
	private void mapInit() {

		// �n�}�^�C�v�ݒ�i1�j
		googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

		// ���݈ʒu�{�^���̕\���i2�j
		googleMap.setMyLocationEnabled(true);

		// �����w�̈ʒu�A�Y�[���ݒ�i3�j
		CameraPosition camerapos = new CameraPosition.Builder()
				.target(new LatLng(35.681382, 139.766084)).zoom(15.5f).build();
		// �n�}�̒��S��ύX����i4�j
		googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(camerapos));

		centerPosition = camerapos;
		execMoyori();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}


	// �n�}�̒��S�ʒu���擾���āAAPI��URL����������
	public void execMoyori() {

		// �n�}�̒��S�ʒu�̎擾
		CameraPosition cameraPos = googleMap.getCameraPosition();

		Bundle bundle = new Bundle();
		// �ܓx
		bundle.putString("y", Double.toString(cameraPos.target.latitude));
		// �o�x
		bundle.putString("x", Double.toString(cameraPos.target.longitude));

		bundle.putString("moyori",
				"http://express.heartrails.com/api/json?method=getStations&");

		// LoaderManager�̏������i1�j
		getLoaderManager().restartLoader(0, bundle, this);

	}

	@Override
	public Loader<String> onCreateLoader(int id, Bundle bundle) {
		HttpAsyncLoader loader = null;

		switch (id) {
		case 0:
			// ���N�G�X�gURL�̑g�ݗ���
			String url = bundle.getString("moyori")
						+ "x=" + bundle.getString("x") + "&"
						+ "y=" + bundle.getString("y");

			loader = new HttpAsyncLoader(this, url);
			// Web API�ɃA�N�Z�X����(2)
			loader.forceLoad();
			break;

		}
		return loader;
	}

	@Override
	public void onLoadFinished(Loader<String> loader, String body) {

		// API�̎擾�Ɏ��s�̏ꍇ
		if (body == null)
			return;

		switch (loader.getId()) {

		case 0:

			// API�̌��ʂ���͂���
			ParseMoyori parse = new ParseMoyori();
			parse.loadJson(body);

			// �}�[�J�[����������폜���Ă���
			googleMap.clear();
			ekiMarkerMap.clear();

			// API�̌��ʂ��}�[�J�[�ɔ��f����i2�j
			for (EkiInfo e : parse.getEkiinfo()) {

				Marker marker = googleMap.addMarker(new MarkerOptions()
						.position(new LatLng(e.y, e.x))
						.title(e.name)
						.snippet(e.line)
						.icon(BitmapDescriptorFactory
								.fromResource(R.drawable.ic_quest))); //(3)

				// �}�[�J�[�Ɖw����ۊǂ��Ă����i4�j
				ekiMarkerMap.put(marker, e);
			}
			break;
		}
	}

	@Override
	public void onLoaderReset(Loader<String> arg0) {
		// TODO �����������ꂽ���\�b�h�E�X�^�u
	}

}
