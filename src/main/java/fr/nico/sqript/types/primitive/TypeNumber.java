package fr.nico.sqript.types.primitive;

import fr.nico.sqript.ScriptManager;
import fr.nico.sqript.compiling.ScriptDecoder;
import fr.nico.sqript.meta.Primitive;
import fr.nico.sqript.structures.ScriptElement;
import fr.nico.sqript.structures.ScriptOperator;
import fr.nico.sqript.structures.IOperation;
import fr.nico.sqript.types.interfaces.ISerialisable;
import fr.nico.sqript.types.ScriptType;
import net.minecraft.nbt.NBTTagCompound;

import java.text.DecimalFormat;

@Primitive(name = "number",
        parsableAs = {TypeString.class},
        pattern = "("+ ScriptDecoder.CAPTURE_FULL_NUMBER+"|"+ScriptDecoder.CAPTURE_FLOAT+")")

public class TypeNumber extends PrimitiveType<Double> implements ISerialisable {



    @Override
    public ScriptElement parse(String typeName) {
        return null;
    }

    @Override
    public String toString() {
        DecimalFormat df = new DecimalFormat("#");
        df.setMaximumFractionDigits(8);
        return df.format(getObject());
    }

    public TypeNumber(String d){
        super(Double.valueOf(d));
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
        compound.setDouble("getObject()",getObject());
        return compound;
    }

    @Override
    public void read(NBTTagCompound compound) {
        setObject(compound.getDouble("getObject()"));
    }

    static{
        ScriptManager.registerBinaryOperation(ScriptOperator.ADD, TypeNumber.class, TypeNumber.class,
                 (a,b) -> new TypeNumber(((TypeNumber)a).getObject()+((TypeNumber)b).getObject()));

        ScriptManager.registerBinaryOperation(ScriptOperator.SUBTRACT, TypeNumber.class, TypeNumber.class,
                 (a,b) -> new TypeNumber(((TypeNumber)a).getObject()-((TypeNumber)b).getObject()));

        ScriptManager.registerBinaryOperation(ScriptOperator.MULTIPLY, TypeNumber.class, TypeNumber.class,
                 (a,b) -> new TypeNumber(((TypeNumber)a).getObject()*((TypeNumber)b).getObject()));

        ScriptManager.registerBinaryOperation(ScriptOperator.DIVIDE, TypeNumber.class, TypeNumber.class,
                 (a,b) -> new TypeNumber(((TypeNumber)a).getObject()/((TypeNumber)b).getObject()));

        ScriptManager.registerUnaryOperation(ScriptOperator.MINUS_UNARY, TypeNumber.class,
                 (a,b) -> new TypeNumber(-((TypeNumber)a).getObject()));

        ScriptManager.registerUnaryOperation(ScriptOperator.PLUS_UNARY, TypeNumber.class,
                 (a,b) -> a);

        ScriptManager.registerUnaryOperation(ScriptOperator.FACTORIAL, TypeNumber.class,
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

        ScriptManager.registerBinaryOperation(ScriptOperator.MT, TypeNumber.class, TypeNumber.class,
                (a, b) ->  {
                    System.out.println((((TypeNumber)a).getObject())>(((TypeNumber)b).getObject()));
            return new TypeBoolean((((TypeNumber)a).getObject())>(((TypeNumber)b).getObject()));});

        ScriptManager.registerBinaryOperation(ScriptOperator.MTE, TypeNumber.class, TypeNumber.class,
                 (a, b) -> new TypeBoolean(((TypeNumber)a).getObject()>=((TypeNumber)b).getObject()));

        ScriptManager.registerBinaryOperation(ScriptOperator.LT, TypeNumber.class, TypeNumber.class,
                 (a, b) ->  {return new TypeBoolean((((TypeNumber)a).getObject())<(((TypeNumber)b).getObject()));});

        ScriptManager.registerBinaryOperation(ScriptOperator.LTE, TypeNumber.class, TypeNumber.class,
                 (a, b) -> new TypeBoolean(((TypeNumber)a).getObject()<=((TypeNumber)b).getObject()));

        ScriptManager.registerBinaryOperation(ScriptOperator.EXP, TypeNumber.class, TypeNumber.class,
                 (a,b) -> new TypeNumber(Math.pow(((TypeNumber)a).getObject(),((TypeNumber)b).getObject())));

        ScriptManager.registerBinaryOperation(ScriptOperator.ADD, TypeNumber.class, TypeString.class,
                 (a,b) -> new TypeString(((TypeNumber)a).getObject()+((TypeString)b).getObject()));

    }

}
