/*******************************************************************************
 * Copyright (c) 2013 hangum.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     hangum - initial API and implementation
 ******************************************************************************/
package com.hangum.tadpole.application.start.dialog.login;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.rap.addons.d3chart.BarChart;
import org.eclipse.rap.addons.d3chart.ChartItem;
import org.eclipse.rap.addons.d3chart.ColorStream;
import org.eclipse.rap.addons.d3chart.Colors;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.widgets.BrowserUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.hangum.tadpole.application.start.BrowserActivator;
import com.hangum.tadpole.application.start.Messages;
import com.hangum.tadpole.commons.admin.core.dialogs.users.NewUserDialog;
import com.hangum.tadpole.commons.exception.TadpoleAuthorityException;
import com.hangum.tadpole.commons.google.analytics.AnalyticCaller;
import com.hangum.tadpole.commons.libs.core.define.PublicTadpoleDefine;
import com.hangum.tadpole.commons.libs.core.define.SystemDefine;
import com.hangum.tadpole.commons.libs.core.googleauth.GoogleAuthManager;
import com.hangum.tadpole.commons.libs.core.mails.dto.SMTPDTO;
import com.hangum.tadpole.commons.util.GlobalImageUtils;
import com.hangum.tadpole.commons.util.IPFilterUtil;
import com.hangum.tadpole.commons.util.RequestInfoUtils;
import com.hangum.tadpole.engine.manager.TadpoleApplicationContextManager;
import com.hangum.tadpole.engine.query.dao.system.UserDAO;
import com.hangum.tadpole.engine.query.sql.TadpoleSystem_UserDBQuery;
import com.hangum.tadpole.engine.query.sql.TadpoleSystem_UserQuery;
import com.hangum.tadpole.preference.get.GetAdminPreference;
import com.hangum.tadpole.session.manager.SessionManager;
import com.swtdesigner.ResourceManager;

/**
 * Tadpole DB Hub User login dialog.
 * support the localization : (http://wiki.eclipse.org/RAP/FAQ#How_to_switch_locale.2Flanguage_on_user_action.3F) 
 * 
 * @author hangum
 *
 */
public class LoginDialog extends Dialog {
	private static final Logger logger = Logger.getLogger(LoginDialog.class);
	
	private int ID_NEW_USER		 	= IDialogConstants.CLIENT_ID 	+ 1;
	private int ID_FINDPASSWORD 	= IDialogConstants.CLIENT_ID 	+ 2;

	/** database list */
	private List listDBMart = new ArrayList();
	
	private Composite compositeLogin;
	private Label lblEmail;
	
	private Button btnCheckButton;
	private Text textEMail;
	private Label lblPassword;
	private Text textPasswd;
	private Label lblLanguage;
	private Combo comboLanguage;
	
	private Group compositeLetter;
	private Label lblSite;
	private Label lblUserGuide;
	private Label lblContact;
	
	String strPaypal = "<form action='https://www.paypal.com/cgi-bin/webscr' method='post' target='_top'> " + //$NON-NLS-1$
						"	<input type='hidden' name='cmd' value='_s-xclick'> " + //$NON-NLS-1$
						"	<input type='hidden' name='encrypted' value='-----BEGIN PKCS7-----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-----END PKCS7-----'> " + //$NON-NLS-1$
						"	<input type='image' src='https://www.paypalobjects.com/en_US/i/btn/btn_donate_LG.gif' border='0' name='submit' alt='PayPal - The safer, easier way to pay online!'> " + //$NON-NLS-1$
						"	</form>"; //$NON-NLS-1$
	
	public LoginDialog(Shell shell) {
		super(shell);
	}
	
	@Override
	public void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText(String.format("%s v%s", SystemDefine.NAME, SystemDefine.MAJOR_VERSION)); //$NON-NLS-1$
		newShell.setImage(GlobalImageUtils.getTadpoleIcon());
	}

	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		GridLayout gridLayout = (GridLayout) container.getLayout();
		gridLayout.numColumns = 2;
		gridLayout.verticalSpacing = 5;
		gridLayout.horizontalSpacing = 5;
		gridLayout.marginHeight = 5;
		gridLayout.marginWidth = 5;
		
		Composite compositeLeftBtn = new Composite(container, SWT.NONE);
		compositeLeftBtn.setLayout(new GridLayout(1, false));
		
		Button button = new Button(compositeLeftBtn, SWT.NONE);
		button.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		button.setImage(ResourceManager.getPluginImage(BrowserActivator.ID, "resources/TadpoleOverView.png")); //$NON-NLS-1$
		
		compositeLogin = new Composite(container, SWT.NONE);
		compositeLogin.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		compositeLogin.setLayout(new GridLayout(3, false));
		
		lblEmail = new Label(compositeLogin, SWT.NONE);
		lblEmail.setText(Messages.get().LoginDialog_1);
		
		textEMail = new Text(compositeLogin, SWT.BORDER);
		textEMail.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if(e.keyCode == SWT.Selection) {
					if(!"".equals(textPasswd.getText())) okPressed(); //$NON-NLS-1$
					else textPasswd.setFocus();
				}
			}
		});
		textEMail.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		btnCheckButton = new Button(compositeLogin, SWT.CHECK);
		btnCheckButton.setText(Messages.get().LoginDialog_9); //$NON-NLS-1$
		
		lblPassword = new Label(compositeLogin, SWT.NONE);
		lblPassword.setText(Messages.get().LoginDialog_4);
		
		textPasswd = new Text(compositeLogin, SWT.BORDER | SWT.PASSWORD);
		textPasswd.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if(e.keyCode == SWT.Selection) {
					okPressed();
				}
			}
		});
		textPasswd.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Button btnLogin = new Button(compositeLogin, SWT.NONE);
		btnLogin.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				okPressed();
			}
		});
		btnLogin.setText(Messages.get().LoginDialog_15);
		
		lblLanguage = new Label(compositeLogin, SWT.NONE);
		lblLanguage.setText(Messages.get().LoginDialog_lblLanguage_text);
		
		comboLanguage = new Combo(compositeLogin, SWT.READ_ONLY);
		comboLanguage.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				changeUILocale();
			}
		});
		comboLanguage.add(Locale.ENGLISH.getDisplayLanguage());
		comboLanguage.add(Locale.KOREAN.getDisplayLanguage());

		comboLanguage.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		
		comboLanguage.setData(Locale.ENGLISH.getDisplayLanguage(), Locale.ENGLISH);
		comboLanguage.setData(Locale.KOREAN.getDisplayLanguage(), Locale.KOREAN);
		
		
//		comboLanguage.select(0);

		// ---------------------  Registered database ----------------------------------------------------
//		try {
//			listDBMart = getDBMart();
//			if(!listDBMart.isEmpty()) {
//				Group grpSponser = new Group(container, SWT.NONE);
//				GridLayout gl_grpSponser = new GridLayout(1, false);
//				gl_grpSponser.verticalSpacing = 0;
//				gl_grpSponser.horizontalSpacing = 0;
//				gl_grpSponser.marginHeight = 0;
//				gl_grpSponser.marginWidth = 0;
//				grpSponser.setLayout(gl_grpSponser);
//				grpSponser.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
//				grpSponser.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLUE));
//				grpSponser.setText(Messages.get().LoginDialog_grpSponser_text);
//				
//				makeBarChart(grpSponser, listDBMart);
//			}
//		} catch(Exception e) {
//			logger.error("get initdata", e); //$NON-NLS-1$
//		}
		
		compositeLetter = new Group(container, SWT.NONE);
		compositeLetter.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));
		GridLayout gl_compositeLetter = new GridLayout(2, false);
		compositeLetter.setLayout(gl_compositeLetter);
		compositeLetter.setText(Messages.get().LoginDialog_grpShowInformation_text);
		
		lblSite = new Label(compositeLetter, SWT.NONE);
		lblSite.setText(Messages.get().LoginDialog_lblSite_text);
		
		Label lblNewLabel = new Label(compositeLetter, SWT.NONE);
		lblNewLabel.setText("<a href='" + Messages.get().LoginDialog_lblNewLabel_text_1 + "' target='_blank'>" + Messages.get().LoginDialog_lblNewLabel_text_1 + "</a>"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		lblNewLabel.setData(RWT.MARKUP_ENABLED, Boolean.TRUE);
		
		lblUserGuide = new Label(compositeLetter, SWT.NONE);
		lblUserGuide.setText(Messages.get().LoginDialog_lblUserGuide_text);
		
		Composite compositeUserGide = new Composite(compositeLetter, SWT.NONE);
		compositeUserGide.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		GridLayout gl_compositeUserGide = new GridLayout(3, false);
		gl_compositeUserGide.verticalSpacing = 1;
		gl_compositeUserGide.horizontalSpacing = 1;
		gl_compositeUserGide.marginHeight = 1;
		gl_compositeUserGide.marginWidth = 1;
		compositeUserGide.setLayout(gl_compositeUserGide);
		
		Label lblUserKor = new Label(compositeUserGide, SWT.NONE);
		lblUserKor.setText("<a href='https://tadpoledbhub.atlassian.net/wiki/pages/viewpage.action?pageId=20578325' target='_blank'>(Korean)</a>"); //$NON-NLS-1$ //$NON-NLS-2$
		lblUserKor.setData(RWT.MARKUP_ENABLED, Boolean.TRUE);
		
		Label lblUserEng = new Label(compositeUserGide, SWT.NONE);
		lblUserEng.setText("<a href='https://github.com/hangum/TadpoleForDBTools/wiki/RDB-User-Guide-Eng' target='_blank'>(English)</a>"); //$NON-NLS-1$ //$NON-NLS-2$
		lblUserEng.setData(RWT.MARKUP_ENABLED, Boolean.TRUE);
		
		Label lblUserIndonesia = new Label(compositeUserGide, SWT.NONE);
		lblUserIndonesia.setText("<a href='https://github.com/hangum/TadpoleForDBTools/wiki/RDB-User-Guide-ID' target='_blank'>(Indonesia)</a>"); //$NON-NLS-1$ //$NON-NLS-2$
		lblUserIndonesia.setData(RWT.MARKUP_ENABLED, Boolean.TRUE);
		
//		Label lblIssues = new Label(compositeLetter, SWT.NONE);
//		lblIssues.setText(Messages.get().LoginDialog_lblIssues_text);
//		
//		Label lblIssue = new Label(compositeLetter, SWT.NONE);
//		lblIssue.setText("<a href='https://github.com/hangum/TadpoleForDBTools/issues' target='_blank'>https://github.com/hangum/TadpoleForDBTools/issues</a>"); //$NON-NLS-1$ //$NON-NLS-2$
//		lblIssue.setData(RWT.MARKUP_ENABLED, Boolean.TRUE);
		
		lblContact = new Label(compositeLetter, SWT.NONE);
		lblContact.setText(Messages.get().LoginDialog_lblContact_text_1);
		
		Label lblContactUrl = new Label(compositeLetter, SWT.NONE);
		try {
			UserDAO systemUserDao = TadpoleApplicationContextManager.getSystemAdmin();
			lblContactUrl.setText(String.format("<a href='mailto:%s'>%s(%s)</a>", systemUserDao.getEmail(), systemUserDao.getName(), systemUserDao.getEmail())); //$NON-NLS-1$ //$NON-NLS-2$
		} catch (Exception e1) {
			lblContactUrl.setText("<a href='mailto:adi.tadpole@gmail.com'>Admin(adi.tadpole@gmail.com)</a>"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		lblContactUrl.setData(RWT.MARKUP_ENABLED, Boolean.TRUE);
		
		Label lblDonation = new Label(compositeLetter, SWT.NONE);
//		lblDonation.setText(Messages.get().LoginDialog_lblDonation_text);

		Browser browser = new Browser(compositeLetter, SWT.NONE);
		browser.setLayoutData(new GridData(SWT.FILL, SWT.RIGHT, false, false, 1, 1));
		browser.setText(strPaypal);
		
		AnalyticCaller.track("login"); //$NON-NLS-1$
		
		initUI();
		
		return compositeLogin;
	}
	
	/** change ui locale */
	private void changeUILocale(){
		Object objLanguage = comboLanguage.getData(comboLanguage.getText());
		if(objLanguage == null) {
			changeUILocale(comboLanguage.getItem(0));
		} else {
			changeUILocale(comboLanguage.getText());
		}
	}
	
	@Override
	protected void buttonPressed(int buttonId) {
		if(buttonId == ID_NEW_USER) {
			newUser();
			textEMail.setFocus();
		} else if(buttonId == ID_FINDPASSWORD) {
			findPassword();
			textEMail.setFocus();
		} else {
			okPressed();
		}
	}
	
	@Override
	protected void okPressed() {
		String strEmail = StringUtils.trimToEmpty(textEMail.getText());
		String strPass = StringUtils.trimToEmpty(textPasswd.getText());

		if(!validation(strEmail, strPass)) return;
		
		try {
			UserDAO userDao = TadpoleSystem_UserQuery.login(strEmail, strPass);
			
			// firsttime email confirm
			if(PublicTadpoleDefine.YES_NO.NO.name().equals(userDao.getIs_email_certification())) {
				InputDialog inputDialog=new InputDialog(getShell(), Messages.get().LoginDialog_10, Messages.get().LoginDialog_17, "", null); //$NON-NLS-3$ //$NON-NLS-1$
				if(inputDialog.open() == Window.OK) {
					if(!userDao.getEmail_key().equals(inputDialog.getValue())) {
						throw new Exception(Messages.get().LoginDialog_19);
					} else {
						TadpoleSystem_UserQuery.updateEmailConfirm(strEmail);
					}
				} else {
					throw new Exception(Messages.get().LoginDialog_20);
				}
			}
			
			if(PublicTadpoleDefine.YES_NO.NO.name().equals(userDao.getApproval_yn())) {
				MessageDialog.openError(getParentShell(), Messages.get().LoginDialog_7, Messages.get().LoginDialog_27);
				
				return;
			}
			
			// Check the allow ip
			String strAllowIP = userDao.getAllow_ip();
			boolean isAllow = IPFilterUtil.ifFilterString(strAllowIP, RequestInfoUtils.getRequestIP());
			if(logger.isDebugEnabled())logger.debug(Messages.get().LoginDialog_21 + userDao.getEmail() + Messages.get().LoginDialog_22 + strAllowIP + Messages.get().LoginDialog_23+ RequestInfoUtils.getRequestIP());
			if(!isAllow) {
				logger.error(Messages.get().LoginDialog_21 + userDao.getEmail() + Messages.get().LoginDialog_22 + strAllowIP + Messages.get().LoginDialog_26+ RequestInfoUtils.getRequestIP());
				MessageDialog.openError(getParentShell(), Messages.get().LoginDialog_7, Messages.get().LoginDialog_28);
				return;
			}
			
			if(PublicTadpoleDefine.YES_NO.YES.name().equals(userDao.getUse_otp())) {
				OTPLoginDialog otpDialog = new OTPLoginDialog(getShell());
				otpDialog.open(); 

				if(!GoogleAuthManager.getInstance().isValidate(userDao.getOtp_secret(), otpDialog.getIntOTPCode())) {
					throw new Exception(Messages.get().LoginDialog_2);
				}
			}
			
			// 로그인 유지.
			registLoginID(userDao.getEmail());
			
			SessionManager.addSession(userDao);
			
			// save login_history
			TadpoleSystem_UserQuery.saveLoginHistory(userDao.getSeq());
		} catch (TadpoleAuthorityException e) {
			logger.error(String.format("Login exception. request email is %s, reason %s", strEmail, e.getMessage())); //$NON-NLS-1$
			MessageDialog.openError(getParentShell(), Messages.get().LoginDialog_29, e.getMessage());
			
			textPasswd.setText("");
			textPasswd.setFocus();
			return;
		} catch (Exception e) {
			logger.error(String.format("Login exception. request email is %s, reason %s", strEmail, e.getMessage())); //$NON-NLS-1$
			MessageDialog.openError(getParentShell(), Messages.get().LoginDialog_29, e.getMessage());
			
			textPasswd.setFocus();
			return;
		}	
		
		super.okPressed();
	}
	
	/**
	 * register login id
	 * 
	 * @param userId
	 */
	private void registLoginID(String userId) {
		if(!btnCheckButton.getSelection()) {
			deleteCookie();
			return;
		}
		
		saveCookie(PublicTadpoleDefine.TDB_COOKIE_USER_SAVE_CKECK, Boolean.toString(btnCheckButton.getSelection()));
		saveCookie(PublicTadpoleDefine.TDB_COOKIE_USER_ID, userId);
		saveCookie(PublicTadpoleDefine.TDB_COOKIE_USER_LANGUAGE, comboLanguage.getText());
	}
	
	private void deleteCookie() {
		try {
			HttpServletResponse response = RWT.getResponse();
			HttpServletRequest request = RWT.getRequest();
			Cookie[] cookies = request.getCookies();
			for (Cookie cookie : cookies) {
				if(PublicTadpoleDefine.TDB_COOKIE_USER_SAVE_CKECK.equals(cookie.getName())) {
					cookie.setMaxAge(0);
					cookie.setPath("/");
					response.addCookie(cookie);
				}
				if(PublicTadpoleDefine.TDB_COOKIE_USER_ID.equals(cookie.getName())) {
					cookie.setMaxAge(0);
					cookie.setPath("/");
					response.addCookie(cookie);
				}
				if(PublicTadpoleDefine.TDB_COOKIE_USER_LANGUAGE.equals(cookie.getName())) {
					cookie.setMaxAge(0);
					cookie.setPath("/");
					response.addCookie(cookie);
				}
			}

		} catch(Exception e) {
			logger.error("regist user info", e);
		}
	}
	
	private void saveCookie(String key, String value) {
		try {
			HttpServletResponse response = RWT.getResponse();
			Cookie tdbCookie = new Cookie(key, value);
			tdbCookie.setMaxAge(60 * 60 * 24 * 365);
			tdbCookie.setPath("/");
			response.addCookie(tdbCookie);
		} catch(Exception e) {
			logger.error("regist user info", e);
		}
	}
		
	@Override
	public boolean close() {
		//  로그인이 안되었을 경우 로그인 창이 남아 있도록...(https://github.com/hangum/TadpoleForDBTools/issues/31)
		if(!SessionManager.isLogin()) return false;
		
		return super.close();
	}

	/**
	 * validation
	 * 
	 * @param strEmail
	 * @param strPass
	 */
	private boolean validation(String strEmail, String strPass) {
		// validation
		if("".equals(strEmail)) { //$NON-NLS-1$
			MessageDialog.openError(getParentShell(), Messages.get().LoginDialog_7, Messages.get().LoginDialog_11);
			textEMail.setFocus();
			return false;
		} else if("".equals(strPass)) { //$NON-NLS-1$
			MessageDialog.openError(getParentShell(), Messages.get().LoginDialog_7, Messages.get().LoginDialog_14);
			textPasswd.setFocus();
			return false;
		}
		
		return true;
	}
	
	/**
	 * Create contents of the button bar.
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
//		createButton(parent, IDialogConstants.OK_ID, Messages.get().LoginDialog_15, true);
		
		createButton(parent, ID_NEW_USER, Messages.get().LoginDialog_button_text_1, false);
		try {
			SMTPDTO smtpDto = GetAdminPreference.getSessionSMTPINFO();
			if(!"".equals(smtpDto.getEmail())) { //$NON-NLS-1$
				createButton(parent, ID_FINDPASSWORD, Messages.get().LoginDialog_lblFindPassword, false);
			}
		} catch (Exception e) {
//			logger.error("view findpasswd button", e);
//			ignore exception
		}
	}
	
	/**
	 * initialize ui
	 */
	private void initUI() {
		String defaultLanguage = RWT.getUISession().getLocale().getDisplayLanguage();
		boolean isFound = false;
		for(String strName : comboLanguage.getItems()) {
			if(strName.equals(defaultLanguage)) {
				isFound = true;
				comboLanguage.setText(strName);
				changeUILocale(comboLanguage.getText());
				break;
			}
		}
		
		// 쿠키에서 사용자 정보를 찾지 못했으면..
		if(!isFound) {
			// 사용자 브라우저 랭귀지를 가져와서, 올챙이가 지원하는 랭귀지인지 검사해서..
			String locale = RequestInfoUtils.getDisplayLocale();
			for(String strLocale : comboLanguage.getItems()) {
				if(strLocale.equals(locale)) {
					isFound = true;
					break;
				}
			}
			// 있으면... 
			if(isFound) comboLanguage.setText(locale);
			else comboLanguage.setText(Locale.ENGLISH.getDisplayLanguage());
			
			// 랭귀지를 바꾸어 준다.
			changeUILocale(comboLanguage.getText());
		}
		
		// find login id
		initCookieData();
		if("".equals(textEMail.getText())) {
			textEMail.setFocus();
		} else {
			textPasswd.setFocus();
		}
		
		// check support browser
		if(!RequestInfoUtils.isSupportBrowser()) {
			String errMsg = Messages.get().LoginDialog_30 + RequestInfoUtils.getUserBrowser() + ".\n" + Messages.get().UserInformationDialog_5 + "\n" + Messages.get().LoginDialog_lblNewLabel_text;  //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
			MessageDialog.openError(getParentShell(), Messages.get().LoginDialog_7, errMsg);
		}
	}
	
	/**
	 * initialize cookie data
	 */
	private void initCookieData() {
		HttpServletRequest request = RWT.getRequest();
		Cookie[] cookies = request.getCookies();
		if(cookies != null) {
			for (Cookie cookie : cookies) {
				boolean isFind = false;
				
				if(PublicTadpoleDefine.TDB_COOKIE_USER_ID.equals(cookie.getName())) {
					textEMail.setText(cookie.getValue());
					isFind = true;
				}
				
				if(isFind) break;
			}
			for (Cookie cookie : cookies) {
				boolean isFind = false;
				if(PublicTadpoleDefine.TDB_COOKIE_USER_SAVE_CKECK.equals(cookie.getName())) {
					btnCheckButton.setSelection(Boolean.parseBoolean(cookie.getValue()));
					isFind = true;
				}
				
				if(isFind) break;
			}
			for (Cookie cookie : cookies) {
				boolean isFind = false;
				if(PublicTadpoleDefine.TDB_COOKIE_USER_LANGUAGE.equals(cookie.getName())) {
					comboLanguage.setText(cookie.getValue());
					changeUILocale();
					isFind = true;
				}
				
				if(isFind) break;
			}
		}
	}
	
	/**
	 * change ui locale
	 * 
	 * @param strComoboStr
	 */
	private void changeUILocale(String strComoboStr) {
		Locale localeSelect = (Locale)comboLanguage.getData(strComoboStr);
		RWT.getUISession().setLocale(localeSelect);
		
		btnCheckButton.setText(Messages.get().LoginDialog_9);
		lblEmail.setText(Messages.get().LoginDialog_1);
		lblPassword.setText(Messages.get().LoginDialog_4);
		lblLanguage.setText(Messages.get().LoginDialog_lblLanguage_text);
		
		compositeLetter.setText(Messages.get().LoginDialog_grpShowInformation_text);
		lblSite.setText(Messages.get().LoginDialog_lblSite_text);
		lblUserGuide.setText(Messages.get().LoginDialog_lblUserGuide_text);
		lblContact.setText(Messages.get().LoginDialog_lblContact_text_1);
		
		compositeLetter.layout();
		compositeLogin.layout();
	}
	
	/**
	 * 데이터베이스 통계 bar chart를 생성합니다. 
	 * 
	 * @param composite
	 * @param listData
	 */
	private void makeBarChart(Composite compositeCursor, List listData) {
		try {
			ColorStream colors = Colors.cat20Colors(compositeCursor.getDisplay()).loop();
			
			BarChart barChart = new BarChart(compositeCursor, SWT.NONE);
			GridLayout gl_grpConnectionInfo = new GridLayout(1, true);
			gl_grpConnectionInfo.verticalSpacing = 0;
			gl_grpConnectionInfo.horizontalSpacing = 0;
			gl_grpConnectionInfo.marginHeight = 0;
			gl_grpConnectionInfo.marginWidth = 0;
			barChart.setLayout(gl_grpConnectionInfo);
			barChart.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
			barChart.setBarWidth(10);
			
			for(Object element : listData) {
				Map<String, Object> retMap = (HashMap<String, Object>)element;
				
				ChartItem item = new ChartItem(barChart);
			    item.setText(retMap.get("dbms_type") + " (" +  retMap.get("tot") + ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			    item.setColor(colors.next());
			    
			    float floatVal = Float.parseFloat(""+retMap.get("tot")) / 300; //$NON-NLS-1$ //$NON-NLS-2$
			    item.setValue(floatVal);
			}
			
			barChart.layout();
			barChart.getParent().layout();
		} catch(Exception e) {
			logger.error("Get registered DB", e); //$NON-NLS-1$
		}
	}
	
	/**
	 * registered database
	 * 
	 * @return
	 */
	private List getDBMart() throws Exception {
		return TadpoleSystem_UserDBQuery.getRegisteredDB();
	}

	private void newUser() {
		NewUserDialog newUser = new NewUserDialog(getParentShell());
		if(Dialog.OK == newUser.open()) {
			String strEmail = newUser.getUserDao().getEmail();
			textEMail.setText(strEmail);
			textPasswd.setFocus();
		}
	}
	
	private void findPassword() {
		FindPasswordDialog dlg = new FindPasswordDialog(getShell());
		dlg.open();
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		if(listDBMart.isEmpty()) {
			return new Point(480, 320);
		} else {
			return new Point(480, 460);
		}
	}
}