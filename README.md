# BitBucketMiner
Herramienta de minería y transformación de modelos para la plataforma de BitBucket, usado para GitMiner.

Aparte del GET y POST obligatorios de implementar, se han implementado GETs individuales de Comment, Commit y Issue para hacer pruebas individuales.

En este proyecto, no se han hecho transformadores aparte, sino que los servicios actuan de recogedores de datos y transformadores. Aun así, el modelo de datos completo que se devuelve se ajusta al modelo de datos del proyecto y funciona con el proyecto de GitMiner.

### Estructura del proyecto
```
aiss.githubminer
    controller
        unitarios
            CommentController
            CommitController
            IssueController
        ProjectController
        
    model
        BitBucketComment
        BitBucketCommit
        BitBucketIssue
        BitBucketProject
        BitBucketUser
        
    service
        CommentService
        CommitService
        IssueService
        ProjectService
        
    BitBucketMinerApplication 
```

