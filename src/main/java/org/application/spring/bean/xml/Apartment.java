package org.application.spring.bean.xml;

public class Apartment {

    private Block block;
    private int code;
    private String name;

    public Apartment() {
        System.out.println("Apartment has been created!");
    }

    public Apartment(Block block, int code, String name) {
        this.block = block;
        this.code = code;
        this.name = name;
        System.out.println("Apartment has been created by constructor injection!");
    }

    public Block getBlock() {
        return block;
    }

    public void setBlock(Block block) {
        this.block = block;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Apartment{" +
                "block=" + block +
                ", code=" + code +
                ", name='" + name + '\'' +
                '}';
    }
}
