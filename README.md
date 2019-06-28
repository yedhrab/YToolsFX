# YToolsJava <!-- omit in toc -->

Kişisel araçlarımı derlediğin JavaFX GUI'si.

> Java 12'nin son sürümü ile yazılmıştır.

## İçerikler <!-- omit in toc -->

- [Tanıtım](#Tan%C4%B1t%C4%B1m)
- [Hazırlanma](#Haz%C4%B1rlanma)
  - [Java 8 Ayarları](#Java-8-Ayarlar%C4%B1)
  - [Java12 Ayarları](#Java12-Ayarlar%C4%B1)
  - [Dosyaları Yapılandırma](#Dosyalar%C4%B1-Yap%C4%B1land%C4%B1rma)
  - [Bağımlılıkları Dahil Etme](#Ba%C4%9F%C4%B1ml%C4%B1l%C4%B1klar%C4%B1-Dahil-Etme)
  - [Scene Builder ile Tasarım Ayarı](#Scene-Builder-ile-Tasar%C4%B1m-Ayar%C4%B1)
  - [Derleme Yapılandırması](#Derleme-Yap%C4%B1land%C4%B1rmas%C4%B1)
- [Ek Scriptler](#Ek-Scriptler)
- [Destek ve İletişim](#Destek-ve-%C4%B0leti%C5%9Fim)

## Tanıtım

- Google drive direct link oluşturma
  - Direct link, tıkladığınız anda indirelebilen linklerdir
- Youtube üzerinden video / ses indirme
  - Videoları `Downloads` dizinine kaydeder.

[![app](res/app.gif)](res/app.mp4)

## Hazırlanma

<kbd>Project Structures</kbd> kısmına erişme kısayolu: <kbd>CTRL</kbd> + <kbd>ALT</kbd> + <kbd>SHIFT</kbd> + <kbd>S</kbd>

> - Test için otomatize edilemsi lazım 😢
> - Her panelin `fxml` dosyası ayrılıp ana `fxml`'e node olarak include edilecek (her fxml tek controller kabul ediyor 😢)

### Java 8 Ayarları

- Direct olarak exe'yi artifact kısmından oluşturabilrisin
- <kbd>Project Structures</kbd> + <kbd>Artifact</kbd> + <kbd>+</kbd> + <kbd>JavaFX Application</kbd>
- `JavaFX` sekmesine gelin
  - **Application Class** kısmından Main methodunuzu seçin (Application'ı extend etmeli)
  - **Native Bundle**'dan istediğinizi seçin (olmazsa `all` deyin)

### Java12 Ayarları

<kbd>Project Structure</kbd> - <kbd>Project</kbd> alanına girin

- **Project SDK** `java version 12.0.1`
- **Project Language Level** - `12`
- **Project compiler output** `~\YToolsJava\out`

> Java 12 ile `exe` oluşturulması basit değil 😢
>
> - [JavaFX 12 exe oluşturma](https://medium.com/@adam_carroll/java-packager-with-jdk11-31b3d620f4a8)
> - [Stackoverflow Sorusu](https://stackoverflow.com/questions/53453212/how-to-deploy-a-javafx-11-desktop-application-with-a-jre)

### Dosyaları Yapılandırma

<kbd>Project Structure</kbd> - <kbd>Project Settings</kbd> - <kbd>Modules</kbd> - <kbd>Source</kbd> sekmesinde alttaki gibi ayarlayın

![](res/intellij_res_file.png)

> Kaynak için [buraya](https://openjfx.io/openjfx-docs/#install-javafx) bakabilirsin.

### Bağımlılıkları Dahil Etme

Proje [JavaFX] ve [JFoenix] framework'ü ve [Gluonhq charm-glisten] modülü ile yapılmıştır.

- [JavaFX], [JFoenix] paketlerini indirin
- [Gluonhq charm-glisten] jar dosyasını indirin _(v6.0.0)_
- <kbd>Project Structure</kbd> - <kbd>Modules</kbd> - <kbd>+</kbd> - <kbd>Library</kbd> ile JavaFX'in lib, JFoenix'in kendisini ve `charm-glisten-6.0.0.jar` dosyasını ekleyin

> Youtube indirici için [youtube-dl](https://yt-dl.org/downloads/2019.06.21/youtube-dl.exe) ve onun gerek duyduğu [Microsoft Visual C++ 2010 Redistributable Package (x86)](https://download.microsoft.com/download/5/B/C/5BC5DBB3-652D-4DCE-B14A-475AB85EEF6E/vcredist_x86.exe) paketi gereklidir.

### Scene Builder ile Tasarım Ayarı

[JFoenix] framework'ü kullanıldığından [Scene Builder]'ın library'lerine dahil edilmesi lazımdır.

- Sol üst kısımda **Library** sekmesinin en sağındaki <kbd>⚙</kbd> tıklayın
- <kbd>JAR/FXML Management</kbd> - <kbd>Add Library/FXML from file system</kbd> linkine tıklayın
- İndirdiğiniz [JFoenix] dosyasının `jar`'ını bulup, seçin.
- <kbd>Check All</kbd> ve <kbd>Built in</kbd> ayarları ile dahil edin.

### Derleme Yapılandırması

<kbd>Edit configuration</kbd> alanına girin

- **Main class** yerine `application.MainApp` yazın
- **VM Options** alanına alttaki metni yazın:
  - `--module-path "C:\Program Files\Java\javafx-sdk-12.0.1\lib" --add-modules=javafx.controls,javafx.fxml`

> `C:\Program Files\Java\javafx-sdk-12.0.1` yerine sizin kendi JavaFX SDK yolunuzu yazın.

## Ek Scriptler

```bat
set PATH_TO_FX_MODS=C:\Program Files\Java\javafx-jmods-12.0.1
set JAVA_HOME=C:\Program Files\Java\jdk-12.0.1
set PATH_TO_JDK_FX=C:\Program Files\Java\jdkfx-12.0.1
"%JAVA_HOME%\bin\jlink" --module-path "%PATH_TO_FX_MODS%" --add-modules java.se,javafx.fxml,javafx.web,javafx.media,javafx.swing --bind-services --output "%PATH_TO_JDK_FX%"

javac --module-path "%PATH_TO_FX_MODS%" --add-modules java.se,javafx.fxml,javafx.web,javafx.media,javafx.swing --bind-services --output "%PATH_TO_JDK_FX%"

set JAVA_HOME=%PATH_TO_JDK_FX%

dir /s /b src\*.java > sources.txt & javac -d mods/hellofx @sources.txt & del sources.txt
java --module-path mods -m hellofx/hellofx.HelloFX

javac --module-path "%PATH_TO_FX_MODS%" --add-modules java.se,javafx.fxml,javafx.web,javafx.media,javafx.swing -d out/ytoolsfx @sources.txt

java --module-path "%PATH_TO_FX_MODS%" --add-modules java.se,javafx.fxml,javafx.web,javafx.media,javafx.swing -d out/ytoolsfx @sources.txt

java --module-path out -m hellofx/hellofx.HelloFX

dir /s /b *.class > sources.txt
```

## Destek ve İletişim

**The [MIT License](https://choosealicense.com/licenses/mit/) &copy; Yunus Emre Ak**

[![Github](https://drive.google.com/uc?id=1PzkuWOoBNMg0uOMmqwHtVoYt0WCqi-O5)][github]
[![LinkedIn](https://drive.google.com/uc?id=1hvdil0ZHVEzekQ4AYELdnPOqzunKpnzJ)][linkedin]
[![Website](https://drive.google.com/uc?id=1wR8Ph0FBs36ZJl0Ud-HkS0LZ9b66JBqJ)][website]
[![Mail](https://drive.google.com/uc?id=142rP0hbrnY8T9kj_84_r7WxPG1hzWEcN)][mail]
[![Destek](https://drive.google.com/uc?id=1zyU7JWlw4sJTOx46gJlHOfYBwGIkvMQs)][bağış anlık]

[![Patreon](https://drive.google.com/uc?id=11YmCRmySX7v7QDFS62ST2JZuE70RFjDG)][bağış aylık]

[javafx]: http://gluonhq.com/download/javafx-12-0-1-sdk-windows/
[jfoenix]: https://search.maven.org/remotecontent?filepath=com/jfoenix/jfoenix/9.0.8/jfoenix-9.0.8.jar
[gluonhq charm-glisten]: https://nexus.gluonhq.com/nexus/content/repositories/releases/com/gluonhq/charm-glisten/
[scene builder]: https://gluonhq.com/products/scene-builder/thanks/?dl=/download/scene-builder-11-windows-x64/

<!-- İletişim -->

[mail]: mailto::yedhrab@gmail.com?subject=YBilgiler%20%7C%20Github
[github]: https://github.com/yedhrab
[website]: https://yemreak.com
[linkedin]: https://www.linkedin.com/in/yemreak/
[bağış anlık]: https://gogetfunding.com/yemreak/
[bağış aylık]: https://www.patreon.com/yemreak/

<!-- İletişim Sonu -->
