package fr.nico.sqript.expressions;

import fr.nico.sqript.meta.Feature;
import fr.nico.sqript.network.ScriptNetworkManager;
import fr.nico.sqript.meta.Expression;
import fr.nico.sqript.structures.ScriptContext;
import fr.nico.sqript.types.ScriptType;
import fr.nico.sqript.types.TypeDictionary;
import fr.nico.sqript.types.TypeNBTTagCompound;
import fr.nico.sqript.types.TypeNull;
import fr.nico.sqript.types.primitive.TypeBoolean;
import fr.nico.sqript.types.primitive.TypeString;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.JsonUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;


@Expression(name = "Network Expressions",
        features = {
                @Feature(name = "Element is synchronized", description = "Returns whether an element has been synchronized from the server.", examples = "\"my_key\" is synced\n", pattern = "value [of] {string} is (synced|synchronized)", type = "boolean"),
                @Feature(name = "Synchronized element", description = "Returns whether an element has been synchronized from the server.", examples = "synced value of \"my_key\"", pattern = "(synced|synchronized) value [of] {string}"),
                @Feature(name = "HTTP request result using POST method", description = "Returns the result of a POST method sent to the given address with the given parameters.", examples = "result of http post \"sqript.fr\" with values (dictionary with [[\"username\",\"nico-\"],[\"password\",314159268]])", pattern = "[result of] http post [to] {string} [with values {nbttagcompound|dictionary|string}]"),
                @Feature(name = "HTTP request result using GET method", description = "Returns the result of a GET method sent to the given address with the given headers.", examples = "result of http post \"sqript.fr\" with headers (dictionary with [[\"username\",\"nico-\"],[\"password\",314159268]])", pattern = "[result of] http get [from] {string} [with header[s] {dictionary}]"),
        }
)
public class ExprNetwork extends ScriptExpression {

    @Override
    public ScriptType get(ScriptContext context, ScriptType[] parameters) {
        switch (getMatchedIndex()) {
            case 0:
                TypeString key = (TypeString) parameters[0];
                return new TypeBoolean(ScriptNetworkManager.syncValue.containsKey(key.getObject()));
            case 1:
                key = (TypeString) parameters[0];
                //System.out.println("Keys : "+ Arrays.toString(ScriptNetworkManager.syncValue.keySet().toArray(new String[0])));
                ScriptType result;
                if ((result = ScriptNetworkManager.get(key.getObject())) != null)
                    return result;
                else return new TypeNull();
            case 2:
                TypeString host = (TypeString) parameters[0];
                HttpPost post = new HttpPost(host.getObject());
                List<NameValuePair> urlParameters = new ArrayList<>();

                // add request parameter, form parameters
                ScriptType data = parameters[1];
                if (data != null) {
                    if (data instanceof TypeDictionary) {
                        TypeDictionary dictionary = (TypeDictionary) data;
                        dictionary.getObject().keySet().forEach(k -> urlParameters.add(new BasicNameValuePair(k.toString(), dictionary.getObject().get(k).toString())));
                    } else {
                        try {
                            post.setEntity(new StringEntity(data.toString()));
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                }

                try {
                    post.setEntity(new UrlEncodedFormEntity(urlParameters));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                try (CloseableHttpClient httpClient = HttpClients.createDefault();
                     CloseableHttpResponse response = httpClient.execute(post)) {
                    return new TypeString(EntityUtils.toString(response.getEntity()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return new TypeNull();
            case 3:
                host = (TypeString) parameters[0];
                HttpGet get = new HttpGet(host.getObject());

                // add request parameter, form parameters
                data = parameters[1];
                if (data != null) {
                    if (data instanceof TypeDictionary) {
                        TypeDictionary dictionary = (TypeDictionary) data;
                        dictionary.getObject().keySet().forEach(k -> get.addHeader(k.toString(), dictionary.getObject().get(k).toString()));
                    }
                }

                try (CloseableHttpClient httpClient = HttpClients.createDefault();
                     CloseableHttpResponse response = httpClient.execute(get)) {
                    return new TypeString(EntityUtils.toString(response.getEntity()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return new TypeNull();
        }
        return null;
    }

    @Override
    public boolean set(ScriptContext context, ScriptType to, ScriptType[] parameters) {
        return false;
    }
}
