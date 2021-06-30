package io.dataspaceconnector.model.infomodel;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Id;
import java.net.URI;
import java.util.Set;
import java.util.UUID;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.TypeSpec;
import io.dataspaceconnector.model.utils.UriConverter;
import io.dataspaceconnector.utils.exceptions.NotImplemented;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;

@interface DefaultImpl { }

@SupportedAnnotationTypes("io.dataspaceconnector.model.infomodel.DefaultImpl")
@SupportedSourceVersion(SourceVersion.RELEASE_11)
class DefaultImplProcessor extends AbstractProcessor {

    Types typeUtils;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        typeUtils = processingEnv.getTypeUtils();
    }

    @SneakyThrows
    @Override
    public boolean process(final Set<? extends TypeElement> annotations,
                           final RoundEnvironment roundEnv) {
        for (final var annotation : annotations) {
            for (final var annotatedElement : roundEnv.getElementsAnnotatedWith(annotation)) {
                if (annotatedElement.getKind() != ElementKind.INTERFACE)
                    continue;

                var newType = TypeSpec.classBuilder(annotatedElement.getSimpleName().toString().substring(1));
                newType.addSuperinterface(Class.forName(String.valueOf(annotatedElement.asType().toString())));
                newType.addAnnotation(AnnotationSpec.builder(Getter.class).build());
                newType.addAnnotation(AnnotationSpec.builder(Setter.class)
                                                    .addMember("value",
                                                               String.valueOf(AccessLevel.PACKAGE))
                                                    .build());

                final var parentInterfaces = typeUtils.directSupertypes(annotatedElement.asType());
                for (final var it : parentInterfaces) {
                    if (it.getKind() == TypeKind.DECLARED) {
                        // this element is super class's element, you can do anything in here
                        Element element = ((DeclaredType) it).asElement();
                        if (element.getKind() != ElementKind.INTERFACE)
                            continue;

                        addMethod(newType, element.asType());
                    }
                }

                final var output = newType.build().toString();
                System.out.println(output);
            }
        }

        return true;
    }

    @SneakyThrows
    private void addMethod(final TypeSpec.Builder type, final TypeMirror typeMirror) {
        Element element = ((DeclaredType)typeMirror).asElement();
        final var enclosed = element.getEnclosedElements();
        for (final var tmp : enclosed) {
            final var methodName = tmp.getSimpleName();
            final var fieldName = getFieldName(methodName.toString());
            final var fieldType = getFieldType((ExecutableElement) tmp);

            var newField = FieldSpec.builder(fieldType, fieldName);

            if (fieldType.equals(UUID.class)) {
                newField.annotations.add(AnnotationSpec.builder(Id.class).build());
            }

            if (fieldType.equals(URI.class)) {
                newField.annotations.add(AnnotationSpec.builder(Convert.class)
                                                       .addMember("converter", UriConverter.class.getCanonicalName() + ".class")
                                                       .build());
                newField.annotations.add(AnnotationSpec.builder(Column.class)
                                                       .addMember("length", "URI_COLUMN_LENGTH")
                                                       .build());
            }

            newField.addModifiers(Modifier.PRIVATE);
            type.addField(newField.build());
        }
    }

    @NotNull
    private Class<?> getFieldType(final ExecutableElement tmp) throws ClassNotFoundException {
        if (tmp.getReturnType().getKind().isPrimitive()) {
            switch (tmp.getReturnType().getKind()) {
                case LONG:
                    return long.class;
                case BOOLEAN:
                    return boolean.class;
                default:
                    throw new NotImplemented();
            }
        }

        return Class.forName(tmp.getReturnType().toString());
    }

    public static String getFieldName(final String methodName) {
        var tmp = methodName;
        if(methodName.startsWith("is"))
            tmp = methodName.substring(2);
        if(methodName.startsWith("get"))
            tmp = methodName.substring(3);

        var c= tmp.toCharArray();
        c[0] = Character.toLowerCase(c[0]);

        return new String(c);
    }
}
