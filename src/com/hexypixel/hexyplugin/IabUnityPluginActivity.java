package com.hexypixel.hexyplugin;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.vending.billing.IInAppBillingService;
import com.hexypixel.hexyplugin.IabHelper.OnIabPurchaseFinishedListener;

import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;

public class IabUnityPluginActivity extends Activity {
	// private static final String purchaseTask = "purchase";
	// private static final String purchase1Task = "purchase1";
	// private static final String consumeAllTask = "consumeAll";
	// private static volatile String taskTitle;
	private static final String tag = "Hulixerian-test-01";
	private static volatile String purchaseItem = null;
	// private static volatile Inventory inventory = null;
	private static volatile int market = IabHelper.MARKET_CANDO;
	private static final int RC_REQUEST = 0;
	private IabHelper mHelper;
	public static volatile String print = "Not initialized";
	private static volatile IabUnityPluginActivity self;
	private static volatile Activity unityActivity;
	private static volatile Intent selfIntent = null;
	public static volatile int iabState = 0;
	public static final int setFState = 1;
	public static final int invFState = 2;
	public static final int purFState = 3;
	public static final int conFState = 4;
	private static volatile String base64EncodedPublicKey;
	// private static volatile boolean purchaseWasOk = false;
	// private static Semaphore lock = new Semaphore(0);

	// private IabHelper.QueryInventoryFinishedListener queLis = new
	// IabHelper.QueryInventoryFinishedListener() {
	// public void onQueryInventoryFinished(IabResult result,
	// Inventory inventory) {
	// IabUnityPluginActivity.inventory = inventory;
	// print += "\nQuery inventory finished. ";
	// Log.d(tag, "Query inventory finished. ");
	// if (result.isFailure()) {
	// print += "\nFailed to query inventory: " + result;
	// Log.d(tag, "Failed to query inventory: " + result);
	// iabState = invFState;
	// gotoUnity();
	// } else {
	// print += "\nQuery inventory was successful: " + result;
	// Log.d(tag, "Query inventory was successful: " + result);
	// if (taskTitle.equals(consumeAllTask)) {
	// List<Purchase> purchases = inventory.getAllPurchases();
	// mHelper.consumeAsync(purchases, conMulLis);
	// } else {
	// mHelper.launchPurchaseFlow(self, purchaseItem, RC_REQUEST,
	// purLis, "payload-string");
	// }
	// }
	// }
	// };

	private IabHelper.OnIabSetupFinishedListener setLis = new IabHelper.OnIabSetupFinishedListener() {
		public void onIabSetupFinished(IabResult result) {
			if (result.isFailure()) {
				print += "\nIAB setup failed: " + result;
				Log.d(tag, "IAB setup failed: " + result);
				iabState = setFState;
				gotoUnity();
			} else {
				print += "\nIAB setup was successful: " + result;
				Log.d(tag, "IAB setup was successful: " + result);
				// if (taskTitle.equals(consumeAllTask)) {
				// List<String> skulist = new ArrayList<String>();
				// skulist.add(purchaseItem);
				// mHelper.queryInventoryAsync(false, skulist, queLis);
				mHelper.launchPurchaseFlow(self, purchaseItem,
						IabHelper.ITEM_TYPE_INAPP, RC_REQUEST, purLis,
						"payload-string");
				// } else {
				// mHelper.launchPurchaseFlow(self, purchaseItem, RC_REQUEST,
				// purLis, "payload-string");
				// }
			}
		}
	};
	private IabHelper.OnIabPurchaseFinishedListener purLis = new IabHelper.OnIabPurchaseFinishedListener() {
		public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
			if (result.isFailure()) {
				print += "\nError purchasing: " + result;
				Log.d(tag, "Error purchasing: " + result);
				iabState = purFState;
				gotoUnity();
			} else if (purchase.getSku().equals(purchaseItem)) {
				print += "\nSuccessful purchase. " + result;
				Log.d(tag, "Successful purchase ." + result);
				mHelper.consumeAsync(purchase, conLis);
			}
		}
	};
	private IabHelper.OnConsumeFinishedListener conLis = new IabHelper.OnConsumeFinishedListener() {
		@Override
		public void onConsumeFinished(Purchase purchase, IabResult result) {
			if (result.isFailure()) {
				print += "\nError Consuming. " + result;
				Log.d(tag, "Error Consuming. " + result);
				iabState = conFState;
			} else {
				print += "\nSuccessful consume. " + result;
				Log.d(tag, "Successful consume. " + result);
			}
			print += "\nPackage name: " + mHelper.mContext.getPackageName();
			print += "\n" + purchase;
			gotoUnity();
		}
	};

	// private IabHelper.OnConsumeMultiFinishedListener conMulLis = new
	// IabHelper.OnConsumeMultiFinishedListener() {
	//
	// @Override
	// public void onConsumeMultiFinished(List<Purchase> purchases,
	// List<IabResult> results) {
	// for (int i = 0; i < purchases.size(); i++) {
	// Purchase purchase = purchases.get(i);
	// IabResult result = results.get(i);
	// if (result.isFailure()) {
	// print += "\nError Consuming. " + purchase + " Result: "
	// + result;
	// Log.d(tag, "Error Consuming. " + purchase + " Result: "
	// + result);
	// iabState = conFState;
	// } else {
	// print += "\nSuccessful consume. " + purchase + " Result: "
	// + result;
	// Log.d(tag, "Successful consume. " + purchase + " Result: "
	// + result);
	// }
	// }
	// gotoUnity();
	// }
	// };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		self = this;
		// if (taskTitle.equals(purchase1Task)) {
		// IabHelper.OnIabPurchaseFinishedListener listener = new
		// IabHelper.OnIabPurchaseFinishedListener() {
		// public void onIabPurchaseFinished(IabResult result,
		// Purchase purchase) {
		// if (result.isFailure()) {
		// print += "\nError in purchasing." + result;
		// Log.d(tag, "Error in purchasing." + result);
		// iabState = purFState;
		// gotoUnity();
		// } else if (purchase.getSku().equals(purchaseItem)) {
		// print += "\nPurchase was ok." + result;
		// Log.d(tag, "Purchase was ok." + result);
		// IabHelper.OnConsumeFinishedListener conListener = new
		// IabHelper.OnConsumeFinishedListener() {
		// @Override
		// public void onConsumeFinished(Purchase purchase,
		// IabResult result) {
		// if (result.isFailure()) {
		// print += "\nError Consuming. " + result;
		// Log.d(tag, "Error Consuming. " + result);
		// iabState = conFState;
		// } else {
		// print += "\nSuccessful consume. " + result;
		// Log.d(tag, "Successful consume. " + result);
		// iabState = 0;
		// }
		// gotoUnity();
		// }
		// };
		// mHelper.consumeAsync(purchase, conListener);
		// } else {
		// gotoUnity();
		// }
		// }
		// };
		// mHelper.launchPurchaseFlow(self, purchaseItem, RC_REQUEST,
		// listener, "payload-string");
		// } else {
		mHelper = new IabHelper(self, base64EncodedPublicKey);
		mHelper.enableDebugLogging(true);
		mHelper.startSetup(setLis, market);
		// }
	}

	private void gotoUnity() {
		finish();
		// lock.release();
	}

	// private static void task(String title) {
	private static void task() {
		// taskTitle = title;
		selfIntent = new Intent((Context) unityActivity,
				IabUnityPluginActivity.class);
		unityActivity.startActivity(selfIntent);
		Thread.yield();
		// try {
		// lock.acquire();
		// } catch (InterruptedException e) {
		// }
	}

	public static void consumeAllPurchaseItem(Activity unityActivity,
			String baseKey, String market) {
		IabUnityPluginActivity.unityActivity = unityActivity;
		base64EncodedPublicKey = baseKey;
		if (market.equals("candoo")) {
			IabUnityPluginActivity.market = IabHelper.MARKET_CANDO;
		} else if (market.equals("cafebazaar")) {
			IabUnityPluginActivity.market = IabHelper.MARKET_BAZAAR;
		}

		// task(consumeAllTask);
	}

	public static void purchase(Activity unityActivity, String item,
			String baseKey, String market) {
		IabUnityPluginActivity.unityActivity = unityActivity;
		IabUnityPluginActivity.purchaseItem = item;
		base64EncodedPublicKey = baseKey;
		if (market.equals("candoo")) {
			IabUnityPluginActivity.market = IabHelper.MARKET_CANDO;
		} else if (market.equals("cafebazaar")) {
			IabUnityPluginActivity.market = IabHelper.MARKET_BAZAAR;
		}
		Log.d(tag, "Purcgase item is: " + item + ".");
		print += "Purcgase item is: " + item + ".";
		// task(purchaseTask);
		task();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		print += "\nonActivityResult(" + requestCode + "," + resultCode + ","
				+ data;
		if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
			super.onActivityResult(requestCode, resultCode, data);
		} else {
			print += "\nonActivityResult handled by IABUtil.";
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mHelper != null)
			mHelper.dispose();
		mHelper = null;
	}

	// Version 1.2

	// public static void setup1(Activity unityActivity, String baseKey,
	// IabHelper.OnIabSetupFinishedListener listener, String market) {
	// IabUnityPluginActivity.unityActivity = unityActivity;
	// IabUnityPluginActivity.base64EncodedPublicKey = baseKey;
	// // mHelper = new IabHelper(unityActivity, base64EncodedPublicKey);
	// if (market.equals("candoo")) {
	// IabUnityPluginActivity.market = IabHelper.MARKET_CANDO;
	// } else if (market.equals("cafebazaar")) {
	// IabUnityPluginActivity.market = IabHelper.MARKET_BAZAAR;
	// }
	// // mHelper.startSetup(listener, IabUnityPluginActivity.market);
	// }
	//
	// public static int purchase1(Activity unityActivity, String item) {
	// IabUnityPluginActivity.unityActivity = unityActivity;
	// purchaseItem = item;
	// task(purchase1Task);
	// return iabState;
	// }
	//
	// // Version 2
	//
	// // Constants
	// private static final int BILLING_RESPONSE_RESULT_OK = 0;
	// private static final int BILLING_RESPONSE_RESULT_USER_CANCELED = 1;
	// private static final int BILLING_RESPONSE_RESULT_BILLING_UNAVAILABLE = 3;
	// private static final int BILLING_RESPONSE_RESULT_ITEM_UNAVAILABLE = 4;
	// private static final int BILLING_RESPONSE_RESULT_DEVELOPER_ERROR = 5;
	// private static final int BILLING_RESPONSE_RESULT_ERROR = 6;
	// private static final int BILLING_RESPONSE_RESULT_ITEM_ALREADY_OWNED = 7;
	// private static final int BILLING_RESPONSE_RESULT_ITEM_NOT_OWNED = 8;
	//
	// // Item types
	// public static final String ITEM_TYPE_INAPP = "inapp";
	// public static final String ITEM_TYPE_SUBS = "subs";
	//
	// // Market parameters
	// static final String CANDO_SERVICE_ACTION =
	// "com.ada.market.service.payment.BIND";
	// static final String CANDO_NAMESPACE = "com.ada.market";
	// static final String BAZAAR_SERVICE_ACTION =
	// "ir.cafebazaar.pardakht.InAppBillingService.BIND";
	// static final String BAZAAR_NAMESPACE = "com.farsitel.bazaar";
	// static final String PLAY_SERVICE_ACTION =
	// "com.android.vending.billing.InAppBillingService.BIND";
	// static final String PLAY_NAMESPACE = "com.android.vending";
	//
	// private static ServiceConnection serviceCon;
	// private static IInAppBillingService service;
	//
	// public static void setup2(Activity unityActivity, String base64,
	// String market) {
	// IabUnityPluginActivity.unityActivity = unityActivity;
	// IabUnityPluginActivity.base64EncodedPublicKey = base64;
	// serviceCon = new ServiceConnection() {
	// @Override
	// public void onServiceDisconnected(ComponentName arg) {
	// service = null;
	// }
	//
	// @Override
	// public void onServiceConnected(ComponentName compName, IBinder srv) {
	// service = IInAppBillingService.Stub.asInterface(srv);
	// try {
	// int response = service.isBillingSupported(3,
	// IabUnityPluginActivity.unityActivity
	// .getPackageName(), ITEM_TYPE_INAPP);
	// if (response != BILLING_RESPONSE_RESULT_OK) {
	// print += "\nError setup. Response: " + response;
	// Log.d(tag, "Error setup. Response: " + response);
	// }
	// service.isBillingSupported(3,
	// IabUnityPluginActivity.unityActivity
	// .getPackageName(), ITEM_TYPE_SUBS);
	// } catch (RemoteException e) {
	// print += "\nError setup. Exception: " + e;
	// Log.d(tag, "Error setup. Exception: " + e);
	// }
	// }
	// };
	// String marketAction;
	// String marketNameSpace;
	// if (market.equalsIgnoreCase("cando")) {
	// marketAction = CANDO_SERVICE_ACTION;
	// marketNameSpace = CANDO_NAMESPACE;
	// } else if (market.equalsIgnoreCase("bazaar")) {
	// marketAction = BAZAAR_SERVICE_ACTION;
	// marketNameSpace = BAZAAR_NAMESPACE;
	// } else {
	// marketAction = PLAY_SERVICE_ACTION;
	// marketNameSpace = PLAY_NAMESPACE;
	// }
	// Intent serviceIntent = new Intent(marketAction);
	// serviceIntent.setPackage(marketNameSpace);
	// if (!unityActivity.getPackageManager()
	// .queryIntentServices(serviceIntent, 0).isEmpty()) {
	// // service available to handle that Intent
	// unityActivity.bindService(serviceIntent, serviceCon,
	// Context.BIND_AUTO_CREATE);
	// } else {
	// print += "\nError setup. Billing service unavailable on device.";
	// Log.d(tag, "Error setup. Billing service unavailable on device.");
	// }
	// }
	//
	// public static void queryInventory2(Activity unityActivity) {
	// }
	//
	// public void purchase(Activity act, String sku, String extraData) {
	// unityActivity = act;
	// // Bundle buyIntentBundle = service.getBuyIntent(3,
	// // act.getPackageName(), sku, ITEM_TYPE_INAPP, extraData);
	// }
}
