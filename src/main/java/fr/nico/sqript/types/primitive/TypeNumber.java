package fr.nico.sqript.types.primitive;

import fr.nico.sqript.ScriptManager;
import fr.nico.sqript.compiling.ScriptDecoder;
import fr.nico.sqript.meta.Primitive;
import fr.nico.sqript.structures.ScriptElement;
import fr.nico.sqript.structures.ScriptOperator;
import fr.nico.sqript.structures.IOperation;
import fr.nico.sqript.types.interfaces.IFormatable;
import fr.nico.sqript.types.interfaces.ISerialisable;
import fr.nico.sqript.types.ScriptType;
import net.minecraft.nbt.NBTTagCompound;

import java.text.DecimalFormat;

@Primitive(name = "number",
        parsableAs = {TypeString.class},
        pattern = "((?:0b|0x)?"+ScriptDecoder.CAPTURE_NUMBER+")")

public class TypeNumber extends PrimitiveType<Double> implements ISerialisable, IFormatable {

    @Override
    public ScriptElement parse(String typeName) {
        return null;
    }

    @Override
    public String toString() {
        DecimalFormat df = new DecimalFormat("#############.#######");
        df.setMaximumFractionDigits(8);
        return df.format(getObject());
    }

    public static Double fromString(String string){
        if(string.startsWith("0x")){
            return (double) Long.parseLong(string.substring(2), 16);
        }
        if(string.startsWith("0b")){
            return (double) Integer.parseInt(string.substring(2), 2);
        }
        return Double.parseDouble(string);
    }

    public TypeNumber(String d){
        super(fromString(d));
    }

    public TypeNumber(Double d){
        super(d);
    }

    public TypeNumber(float f) {
        this((double)f);
    }

    public TypeNumber(int d){
        this((double)d);
    }

    @Override
    public NBTTagCompound write(NBTTagCompound compound) {
        compound.setDouble("object",getObject());
        return compound;
    }

    @Override
    public void read(NBTTagCompound compound) {
        setObject(compound.getDouble("object"));
    }

    static{
        ScriptManager.registerBinaryOperation(ScriptOperator.ADD, TypeNumber.class, TypeNumber.class, TypeNumber.class,
                 (a,b) -> new TypeNumber(((TypeNumber)a).getObject()+((TypeNumber)b).getObject()));

        ScriptManager.registerBinaryOperation(ScriptOperator.SUBTRACT, TypeNumber.class, TypeNumber.class, TypeNumber.class,
                 (a,b) -> new TypeNumber(((TypeNumber)a).getObject()-((TypeNumber)b).getObject()));

        ScriptManager.registerBinaryOperation(ScriptOperator.MULTIPLY, TypeNumber.class, TypeNumber.class, TypeNumber.class,
                 (a,b) -> new TypeNumber(((TypeNumber)a).getObject()*((TypeNumber)b).getObject()));

        ScriptManager.registerBinaryOperation(ScriptOperator.MOD, TypeNumber.class, TypeNumber.class, TypeNumber.class,
                (a,b) -> new TypeNumber(((TypeNumber)a).getObject()%((TypeNumber)b).getObject()));

        ScriptManager.registerBinaryOperation(ScriptOperator.QUOTIENT, TypeNumber.class, TypeNumber.class, TypeNumber.class,
                (a,b) -> new TypeNumber(Math.floorDiv(((TypeNumber)a).getObject().intValue(),((TypeNumber)b).getObject().intValue())));

        ScriptManager.registerBinaryOperation(ScriptOperator.DIVIDE, TypeNumber.class, TypeNumber.class, TypeNumber.class,
                 (a,b) -> new TypeNumber(((TypeNumber)a).getObject()/((TypeNumber)b).getObject()));

        ScriptManager.registerUnaryOperation(ScriptOperator.MINUS_UNARY, TypeNumber.class, TypeNumber.class,
                 (a,b) -> new TypeNumber(-((TypeNumber)a).getObject()));

        ScriptManager.registerUnaryOperation(ScriptOperator.PLUS_UNARY, TypeNumber.class, TypeNumber.class,
                 (a,b) -> a);


        ScriptManager.registerUnaryOperation(ScriptOperator.FACTORIAL, TypeNumber.class, TypeNumber.class,
                new IOperation() {
                    double factorial(double n){
                        if (n <= 0)
                            return 1;
                        else
                            return(n * factorial(n-1));
                    }
                    @Override
                    public ScriptType<?> operate(ScriptType<?> o1, ScriptType<?> o2) {
                        return new TypeNumber(factorial(((TypeNumber)o1).getObject()));
                    }
                }
        );

        ScriptManager.registerBinaryOperation(ScriptOperator.MT, TypeNumber.class, TypeNumber.class, TypeNumber.class,
                (a, b) ->
            new TypeBoolean((((TypeNumber)a).getObject())>(((TypeNumber)b).getObject())));

        ScriptManager.registerBinaryOperation(ScriptOperator.MTE, TypeNumber.class, TypeNumber.class, TypeNumber.class,
                 (a, b) -> new TypeBoolean(((TypeNumber)a).getObject()>=((TypeNumber)b).getObject()));

        ScriptManager.registerBinaryOperation(ScriptOperator.LT, TypeNumber.class, TypeNumber.class, TypeNumber.class,
                 (a, b) -> new TypeBoolean((((TypeNumber)a).getObject())<(((TypeNumber)b).getObject())));

        ScriptManager.registerBinaryOperation(ScriptOperator.LTE, TypeNumber.class, TypeNumber.class, TypeNumber.class,
                 (a, b) -> new TypeBoolean(((TypeNumber)a).getObject()<=((TypeNumber)b).getObject()));

        ScriptManager.registerBinaryOperation(ScriptOperator.EXP, TypeNumber.class, TypeNumber.class, TypeNumber.class,
                 (a,b) -> new TypeNumber(Math.pow(((TypeNumber)a).getObject(),((TypeNumber)b).getObject())));

        ScriptManager.registerBinaryOperation(ScriptOperator.ADD, TypeNumber.class, TypeString.class, TypeNumber.class,
                 (a,b) -> new TypeString(((TypeNumber)a).getObject()+((TypeString)b).getObject()));

    }

    @Override
    public String format(String format) {
        //System.out.println("Formatiing with :"+format);
        DecimalFormat decimalFormat = new DecimalFormat(format.replaceAll("-","#"));
        return decimalFormat.format(getObject());
    }
}
