package consulo.spring.module.extension;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.spring.facet.SpringFileSet;
import consulo.annotation.access.RequiredReadAction;
import consulo.disposer.Disposable;
import consulo.module.extension.ModuleExtension;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;

/**
 * @author VISTALL
 * @since 14-Jan-17
 */
public interface SpringModuleExtension extends ModuleExtension<SpringModuleExtension>, Disposable
{
  @Nullable
  @RequiredReadAction
  static SpringModuleExtension getInstance(@Nonnull Module module) {
    return ModuleUtilCore.getExtension(module, SpringModuleExtension.class);
  }

  @Nonnull
  Set<SpringFileSet> getFileSets();
}
