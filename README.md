# YToolsJava

Kişisel araçlarımı derlediğin JavaFX GUI'si.

> Java 12'nin son sürümü ile yazılmıştır.

## Örnek Görüntü

![app](res/app.gif)

## Hazırlanma

- Project Structures <kbd>CTRL</kbd> + <kbd>ALT</kbd> + <kbd>SHIFT</kbd> + <kbd>S</kbd>
- `Project` - `Project Language Level` - `12`
- `Modules` - `+` - `Library` ile `lib` dizinindeki modülleri ekleyin
- JavaFX, JFoenix kütüphaneleri dahil edin (jmods edilmeyecek)
- `Edit configuration` ile `VM Options` alanına alttaki metni yazın:
- `--module-path "lib\javafx-sdk-12.0.1\lib" --add-modules=javafx.controls,javafx.fxml`

> Kaynak için [buraya](https://openjfx.io/openjfx-docs/#install-javafx) bakabilirsin.
