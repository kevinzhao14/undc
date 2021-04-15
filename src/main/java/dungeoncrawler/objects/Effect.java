package dungeoncrawler.objects;

public class Effect {
    private EffectType type;
    //amount of effect per occurence
    private double amount;
    //seconds per occurence
    private double delay;
    private double duration;

    public Effect(EffectType type, double amount, double delay, double duration) {
        this.type = type;
        this.amount = amount;
        this.delay = delay;
        this.duration = duration;
    }

    public EffectType getType() {
        return type;
    }

    public double getAmount() {
        return amount;
    }

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }
}
