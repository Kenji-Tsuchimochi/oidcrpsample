# Google OAuth Client Library for Java を使用したOpenID Connect RP実装について
この章では Google社が提供しているOAuthのJavaライブラリを使用したRPの実装について記述する
1. 使用するソースコードについて  
この章で使用するソースコードについては  
https://github.com/Kenji-Tsuchimochi/oidcrpsample  
よりダウンロードが可能
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
