# Google OAuth Client Library for Java を使用したOpenID Connect RP実装について
RPの実装方法には、OpenID Connect と SCIM の エンタープライズ実装ガイドライン
( https://www.openid.or.jp/news/eiwg_implementation_guideline_1.0.pdf )
の付録C.5にあるようにApacheのmod_auth_openidcを使用した方法があるが、  
Webサーバの設定を変更できない場合は自力で作成する必要がある。  
この章では Google社が提供しているOAuthのJavaライブラリを使用して作成したRPのサンプルについて解説する。  
IdPは、 [OpenID Certified Mark](https://openid.net/certification/) を 取得している [Yahoo! ID連携 v2](https://developer.yahoo.co.jp/yconnect/v2/) を使用する。
1. 使用するソースコードについて  
この章で使用するソースコードは
https://github.com/Kenji-Tsuchimochi/oidcrpsample  
で公開している
1. ライブラリの入手方法について  
Webブラウザで  
https://developers.google.com/api-client-library/java/google-oauth-java-client/download  
を表示し、google-oauth-java-client-featured.zip のリンクをクリックする
1. 各クラスの役割について  
各クラスの役割は下記の通りとなっている

|クラス名|役割|
|:---|:---|
|OIDCIndex|RPのトップページ。stateとnonceをセッションに設定し、「〇〇でログイン」のリンクを出力する|
|OIDCStart|RPの認証開始ページ。IdPのログインページにリダイレクトする|
|OIDCCallback|エンドユーザの認可が正常終了した場合にIdPからコールバックされるページ。<br />1. アクセストークンの取得<br />2. IdTokenの検証<br />3. UserInfoAPIの実行<br >を行う|
|OIDCUtil|ユーティリティクラス。at_hash検証用の文字列の作成、公開鍵の取得、UserInfoAPIの呼び出しを行う|
|OIDCConsts|定数を管理するクラス|
