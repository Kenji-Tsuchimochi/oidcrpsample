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
|OIDCIndex|RPのトップページ。「〇〇でログイン」の文章を|
