package aquarion.annotations.processors.impl;

import aquarion.annotations.Annotations.LoadRegs;
import aquarion.annotations.processors.BaseProcessor;
import arc.Core;
import arc.graphics.g2d.TextureRegion;
import arc.struct.ObjectSet;
import arc.util.Strings;
import com.squareup.javapoet.*;

import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedOptions;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/** @author GlennFolker */
@SupportedOptions({"modName"})
public class LoadProcessor extends BaseProcessor{
    {
        rounds = 1;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Collections.unmodifiableSet(new HashSet<>(Collections.singletonList(
                LoadRegs.class.getCanonicalName()
        )));
    }

    @Override
    public void process(RoundEnvironment roundEnv) throws Exception{
        if(round == 1){
            TypeSpec outline = TypeSpec.annotationBuilder("Outline").addModifiers(Modifier.PUBLIC)
                .addAnnotation(
                    AnnotationSpec.builder(cName(Target.class))
                        .addMember("value", "$T.$L", cName(ElementType.class), "FIELD")
                    .build()
                )
                .addAnnotation(
                    AnnotationSpec.builder(cName(Retention.class))
                        .addMember("value", "$T.$L", cName(RetentionPolicy.class), "RUNTIME")
                    .build()
                )
                .addMethod(
                    MethodSpec.methodBuilder("color")
                        .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                        .returns(cName(String.class))
                        .build()
                )
                .addMethod(
                    MethodSpec.methodBuilder("radius")
                        .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                        .returns(TypeName.INT)
                        .build()
                )
                .build();

            TypeSpec.Builder spec = TypeSpec.classBuilder("Regions").addModifiers(Modifier.PUBLIC)
                .addJavadoc("Generic texture regions")
                .addType(outline);

            MethodSpec.Builder load = MethodSpec.methodBuilder("load").addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addJavadoc("Loads the texture regions");

            ObjectSet<String> processed = new ObjectSet<>();
            for(Element e : roundEnv.getElementsAnnotatedWith(LoadRegs.class)){
                LoadRegs ann = annotation(e, LoadRegs.class);

                for(String reg : ann.value()){
                    if(!processed.add(reg)) continue;

                    String name = Strings.kebabToCamel(reg);

                    spec.addField(
                        FieldSpec.builder(
                            cName(TextureRegion.class),
                            name + "Region",
                            Modifier.PUBLIC, Modifier.STATIC
                        ).build()
                    );
                    load.addStatement("$L = $T.atlas.find($S)", name + "Region", cName(Core.class), modName + "-" + reg);

                    if(ann.outline()){
                        spec.addField(
                            FieldSpec.builder(
                                cName(TextureRegion.class),
                                name + "OutlineRegion",
                                Modifier.PUBLIC, Modifier.STATIC
                            )
                                .addAnnotation(
                                    AnnotationSpec.builder(ClassName.get(generatedPackageName, "Regions", outline.name))
                                        .addMember("color", "$S", ann.outlineColor())
                                        .addMember("radius", "$L", ann.outlineRadius())
                                        .build()
                                )
                                .build()
                        );

                        load.addStatement("$L = $T.atlas.find($S)", name + "OutlineRegion", cName(Core.class), modName + "-" + reg + "-outline");
                    }
                }
            }

            write(spec
                .addMethod(load.build())
                .build()
            );
        }
    }
}
