package doan.middle_project.exception;

public class NotFoundException extends RuntimeException{
    public NotFoundException(int Errorcode,String message){
        super(message);
    }
}
