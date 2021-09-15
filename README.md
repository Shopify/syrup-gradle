# syrup-gradle

A Gradle plugin for generating GraphQL models with [Shopify/syrup](https://github.com/shopify/syrup)

## Configuration

`generateModels` Gradle task :

```groovy
syrup {
    ShopifyAdmin {
        config = "shopify-syrup.yml"
        graphql = "shopify-graphql"
    }
    
    Swapi {
        config = "syrup.yml"
        graphql = "graphql"
        format = true
        generateReport = false
    }
}
```

`generateSupportFiles` task:

```groovy
syrupSupport {
    configFile = file('syrup.yml')
}
```

Manually specify location of syrup binary:
```groovy
project.ext.syrupBin = "<PATH_TO_SYRUP_BIN>"
```
