package com.intellij.spring.factories.resolvers;

import java.util.Collections;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.jetbrains.annotations.NonNls;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiPrimitiveType;
import com.intellij.psi.PsiType;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.spring.factories.ObjectTypeResolver;
import com.intellij.spring.model.xml.CommonSpringBean;
import com.intellij.spring.model.xml.util.SpringConstant;

/**
 * @author Taras Tielkes
 */
public class UtilConstantTypeResolver implements ObjectTypeResolver {
  @NonNls private static final String FACTORY_CLASS = "org.springframework.beans.factory.config.FieldRetrievingFactoryBean";
  @NonNls private static final char SEPARATOR = '.';

  @Nonnull
  public Set<String> getObjectType(@Nonnull final CommonSpringBean context) {
    if (context instanceof SpringConstant) {
      final SpringConstant constant = (SpringConstant)context;
      final String staticField = StringUtil.notNullize(constant.getStaticField().getStringValue());

      final int lastDotIndex = staticField.lastIndexOf(SEPARATOR);
      if (lastDotIndex != -1) {
        final String className = staticField.substring(0, lastDotIndex);
        final String fieldName = staticField.substring(lastDotIndex + 1);

        final PsiClass psiClass = findClassByExternalName(context, className);
        if (psiClass != null) {
          final PsiField psiField = psiClass.findFieldByName(fieldName, true);
          if (psiField != null) {
            final PsiType type = psiField.getType();
            if (type instanceof PsiPrimitiveType) {
              final String boxedTypeName = ((PsiPrimitiveType)type).getBoxedTypeName();
              return Collections.singleton(boxedTypeName);
            }
            if (type instanceof PsiClassType) {
              final PsiClass typeClass = ((PsiClassType)type).resolve();
              if (typeClass != null) {
                final String qualifiedName = typeClass.getQualifiedName();
                if (qualifiedName != null) {
                  return Collections.singleton(qualifiedName);
                }
              }
            }
          }
        }
      }
    }

    return Collections.emptySet();
  }

  @Nullable
  private static PsiClass findClassByExternalName(@Nonnull final CommonSpringBean context, @Nonnull final String externalName) {
    final Module module = context.getModule();
    if (module != null) {
      final GlobalSearchScope scope = GlobalSearchScope.moduleWithDependenciesAndLibrariesScope(module, false);
      final PsiManager psiManager = context.getPsiManager();
      final String className = externalName.replace('$', '.');
      return JavaPsiFacade.getInstance(psiManager.getProject()).findClass(className, scope);
    }
    return null;
  }

  public boolean accept(@Nonnull final String factoryClassName) {
    return factoryClassName.equals(FACTORY_CLASS);
  }
}
