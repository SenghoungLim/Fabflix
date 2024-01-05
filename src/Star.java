public class Star {
    private final String id;
    private final String name;
    private String DOB;
    public Star(String id, String name, String DOB) {
        this.id = id;
        this.name = name;
        this.DOB = DOB;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDOB() {
        return DOB;
    }

    public void setDOB(String DOB){
        this.DOB = DOB;
    }
}