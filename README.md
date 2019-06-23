# YToolsJava <!-- omit in toc -->

Kişisel araçlarımı derlediğin JavaFX GUI'si.

> Java 12'nin son sürümü ile yazılmıştır.

## İçerikler <!-- omit in toc -->

- [Örnek Görüntü](#%C3%96rnek-G%C3%B6r%C3%BCnt%C3%BC)
- [Hazırlanma](#Haz%C4%B1rlanma)
  - [Java12 Fonksiyonları Aktif Etme](#Java12-Fonksiyonlar%C4%B1-Aktif-Etme)
  - [Bağımlılıkları Dahil Etme](#Ba%C4%9F%C4%B1ml%C4%B1l%C4%B1klar%C4%B1-Dahil-Etme)
  - [Dosyaları Yapılandırma](#Dosyalar%C4%B1-Yap%C4%B1land%C4%B1rma)
- [Destek ve İletişim](#Destek-ve-%C4%B0leti%C5%9Fim)

## Örnek Görüntü

![app](res/app.gif)

## Hazırlanma

> Project Structures <kbd>CTRL</kbd> + <kbd>ALT</kbd> + <kbd>SHIFT</kbd> + <kbd>S</kbd>

### Java12 Fonksiyonları Aktif Etme

- `Project Structure` - `Project` - `Project Language Level` - `12`

### Bağımlılıkları Dahil Etme

- `Project Structure` - `Modules` - `+` - `Library` ile `lib` dizinindeki modülleri ekleyin
- JavaFX, JFoenix kütüphaneleri dahil edin (jmods edilmeyecek)
- `Edit configuration` ile `VM Options` alanına alttaki metni yazın:
- `--module-path "lib\javafx-sdk-12.0.1\lib" --add-modules=javafx.controls,javafx.fxml`

### Dosyaları Yapılandırma

- `Project Structure` - `Project Settings` - `Modules`
- `Source` sekmesinde alttaki gibi ayarlayın

![](res/intellij_res_file.png)

> Kaynak için [buraya](https://openjfx.io/openjfx-docs/#install-javafx) bakabilirsin.

## Destek ve İletişim

**The [MIT License](https://choosealicense.com/licenses/mit/) &copy; Yunus Emre Ak**

[![Github](https://drive.google.com/uc?id=1PzkuWOoBNMg0uOMmqwHtVoYt0WCqi-O5)][github]
[![LinkedIn](https://drive.google.com/uc?id=1hvdil0ZHVEzekQ4AYELdnPOqzunKpnzJ)][linkedin]
[![Website](https://drive.google.com/uc?id=1wR8Ph0FBs36ZJl0Ud-HkS0LZ9b66JBqJ)][website]
[![Mail](https://drive.google.com/uc?id=142rP0hbrnY8T9kj_84_r7WxPG1hzWEcN)][mail]
[![Destek](https://drive.google.com/uc?id=1zyU7JWlw4sJTOx46gJlHOfYBwGIkvMQs)][bağış anlık]

[![Patreon](https://drive.google.com/uc?id=11YmCRmySX7v7QDFS62ST2JZuE70RFjDG)][bağış aylık]

<!-- İletişim -->

[mail]: mailto::yedhrab@gmail.com?subject=YBilgiler%20%7C%20Github
[github]: https://github.com/yedhrab
[website]: https://yemreak.com
[linkedin]: https://www.linkedin.com/in/yemreak/
[bağış anlık]: https://gogetfunding.com/yemreak/
[bağış aylık]: https://www.patreon.com/yemreak/

<!-- İletişim Sonu -->
