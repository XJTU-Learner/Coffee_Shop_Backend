package org.xjtu_learner.coffee_shop;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;

import java.util.Collections;

public class Generator {
    public static void main(String[] args) {
        FastAutoGenerator.create("jdbc:mysql://localhost:3306/coffee_shop", "root", "Xzh040305") // jdbc连接
                .globalConfig(builder -> {
                    builder.author("xuezhihengg") // 设置作者
                            .outputDir("src/main/java"); // 指定输出目录
                })
                .packageConfig(builder ->
                        builder.parent("org.xjtu_learner") // 设置父包名
                                .moduleName("coffee_shop") // 设置父包模块名
                                .entity("entity.po") // 设置 Entity 包名
                                .service("service") // 设置 Service 包名
                                .serviceImpl("service.impl") // 设置 Service Impl 包名
                                .mapper("dao") // 设置 Mapper 包名
                                .pathInfo(Collections.singletonMap(OutputFile.xml, "/Users/xuezhihengg/Projects/coffee_shop/src/main/resources/mapper")) // 设置mapperXml生成路径
                                .controller("controller") // 设置 Controller 包名
                )
                .strategyConfig(builder -> {
                    builder.addInclude()
                            .addTablePrefix("tb_")
                            .entityBuilder()
                            .enableLombok() // 启用 Lombok
                            .enableTableFieldAnnotation() // 启用字段注解
                            .controllerBuilder()
                            .enableRestStyle(); // 启用 REST 风格
                })
                .templateEngine(new FreemarkerTemplateEngine()) // 使用Freemarker引擎模板，默认的是Velocity引擎模板
                .execute();
    }
}


