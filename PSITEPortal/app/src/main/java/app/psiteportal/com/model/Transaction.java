package app.psiteportal.com.model;

/**
 * Created by Lawrence on 2/22/2016.
 */
public class Transaction {

    private String transactionId;
    private String sid;
    private String pid;
    private String itemName;
    private String transactionAmount;
    private String checkNumber;
    private String transactionImg;
    private String transactionType;
    private String transactionDate;


    private Transaction(){
        //empty constructor
    }


    public Transaction(String transactionId, String sid, String pid, String itemName, String transactionAmount, String checkNumber, String transactionImg, String transactionType, String transactionDate) {
        this.transactionId = transactionId;
        this.sid = sid;
        this.pid = pid;
        this.itemName = itemName;
        this.transactionAmount = transactionAmount;
        this.checkNumber = checkNumber;
        this.transactionImg = transactionImg;
        this.transactionType = transactionType;
        this.transactionDate = transactionDate;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(String transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public String getTransactionImg() {
        return transactionImg;
    }

    public void setTransactionImg(String transactionImg) {
        this.transactionImg = transactionImg;
    }

    public String getTransactionAmount() {
        return transactionAmount;
    }

    public void setTransactionAmount(String transactionAmount) {
        this.transactionAmount = transactionAmount;
    }

    public String getCheckNumber() {
        return checkNumber;
    }

    public void setCheckNumber(String checkNumber) {
        this.checkNumber = checkNumber;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }
}
