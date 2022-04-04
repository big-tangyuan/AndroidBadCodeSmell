package tang.Smells;

import java.io.File;

/**
 * @Author TangZT
 */
public class CodeSmell {
    private String name;
    private File file;
    private String info;
    public CodeSmell(String name, File file, String info){
        this.name = name;
        this.file = file;
        this.info = info;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
}