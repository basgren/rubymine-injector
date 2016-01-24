package net.bitpot.injector;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializer;
import net.bitpot.injector.config.ApplicationConfig;
import org.jdom.Element;
import org.jetbrains.annotations.Nullable;

@State(
    name="InjectorSettings",
    storages= {
        @Storage(
            id="other",
            file = "$APP_CONFIG$/injector.xml"
        )}
)
public class InjectorOptionsProvider implements PersistentStateComponent<Element> {

    private ApplicationConfig appConfig = new ApplicationConfig();

    public static InjectorOptionsProvider getInstance() {
        return ServiceManager.getService(InjectorOptionsProvider.class);
    }

    public ApplicationConfig getConfig() {
        return appConfig;
    }

    /**
     * @return a component state. All properties and public fields are serialized. Only values, which differ
     * from default (i.e. the value of newly instantiated class) are serialized.
     * @see XmlSerializer
     */
    @Nullable
    @Override
    public Element getState()
    {
        return appConfig.saveToXML();
    }

    /**
     * This method is called when new component state is loaded. A component should expect this method
     * to be called at any moment of its lifecycle. The method can and will be called several times, if
     * config files were externally changed while IDEA running.
     * @param elem loaded component state
     * @see com.intellij.util.xmlb.XmlSerializerUtil#copyBean(Object, Object)
     */
    @Override
    public void loadState(Element elem)
    {
        appConfig.loadFromXml(elem);
    }
}
