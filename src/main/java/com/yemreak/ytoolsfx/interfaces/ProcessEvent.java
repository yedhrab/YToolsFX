package interfaces;


@FunctionalInterface
public interface ProcessEvent {

    void onOutputChanged(String output);
}
