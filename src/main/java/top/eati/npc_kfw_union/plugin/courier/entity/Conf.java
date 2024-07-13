package top.eati.npc_kfw_union.plugin.courier.entity;

public class Conf implements Cloneable{
    private String miraiHttpServerUrl;
    private String miraiHttpServerToken;
    private long qqBotId;
    private long qqGroupId;
    private boolean disabled = false;

    public Conf(String miraiHttpServerUrl, String miraiHttpServerToken, long qqBotId, long qqGroupId, boolean disabled) {
        this.miraiHttpServerUrl = miraiHttpServerUrl;
        this.miraiHttpServerToken = miraiHttpServerToken;
        this.qqBotId = qqBotId;
        this.qqGroupId = qqGroupId;
        this.disabled = disabled;
    }

    public Conf(boolean disabled) {
        this.disabled = disabled;
    }


    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public String getMiraiHttpServerUrl() {
        return miraiHttpServerUrl;
    }

    public void setMiraiHttpServerUrl(String miraiHttpServerUrl) {
        this.miraiHttpServerUrl = miraiHttpServerUrl;
    }

    public String getMiraiHttpServerToken() {
        return miraiHttpServerToken;
    }

    public void setMiraiHttpServerToken(String miraiHttpServerToken) {
        this.miraiHttpServerToken = miraiHttpServerToken;
    }

    public long getQqBotId() {
        return qqBotId;
    }

    public void setQqBotId(long qqBotId) {
        this.qqBotId = qqBotId;
    }

    public long getQqGroupId() {
        return qqGroupId;
    }

    public void setQqGroupId(long qqGroupId) {
        this.qqGroupId = qqGroupId;
    }

    @Override
    public Conf clone() {
        try {
            Conf clone = (Conf) super.clone();
            // TODO: copy mutable state here, so the clone can't change the internals of the original
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    public static final class Builder {
        private String miraiHttpServerUrl;
        private String miraiHttpServerToken;
        private long qqBotId;
        private long qqGroupId;
        private boolean disabled;

        private Builder() {
        }

        public static Builder aConf() {
            return new Builder();
        }

        public Builder withMiraiHttpServerUrl(String miraiHttpServerUrl) {
            this.miraiHttpServerUrl = miraiHttpServerUrl;
            return this;
        }

        public Builder withMiraiHttpServerToken(String miraiHttpServerToken) {
            this.miraiHttpServerToken = miraiHttpServerToken;
            return this;
        }

        public Builder withQqBotId(long qqBotId) {
            this.qqBotId = qqBotId;
            return this;
        }

        public Builder withQqGroupId(long qqGroupId) {
            this.qqGroupId = qqGroupId;
            return this;
        }

        public Builder withDisabled(boolean disabled) {
            this.disabled = disabled;
            return this;
        }

        public Conf build() {
            Conf conf = new Conf(disabled);
            conf.setMiraiHttpServerUrl(miraiHttpServerUrl);
            conf.setMiraiHttpServerToken(miraiHttpServerToken);
            conf.setQqBotId(qqBotId);
            conf.setQqGroupId(qqGroupId);
            return conf;
        }
    }
}
