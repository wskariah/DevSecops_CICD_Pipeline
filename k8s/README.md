
The Directory Structure for Kustomize

├── base/
│   ├── deployment.yaml
│   ├── service.yaml
│   └── kustomization.yaml
└── overlays/
    ├── dev/
    │   ├── deployment-patch.yaml
    │   └── kustomization.yaml
    ├── staging/
    │   ├── deployment-patch.yaml
    │   └── kustomization.yaml
    └── prod/
        ├── deployment-patch.yaml
        └── kustomization.yaml

Kustomize process:
    Building YAML resources based on a Kustomization file.
    Overriding certain values like image tags, replica counts, or environment variables.
    Applying the final output to a Kubernetes cluster.

kustomization.yaml
    A kustomization.yaml file is the main file ,  It defines the resources, patches, and configurations for your project. 
    The file allows you to specify what should be included in the customization and how to patch resources for different environments

Bases and Overlays
    Base: The base directory contains the common, shared configuration files (e.g., deployment.yaml, service.yaml) that are used across different environments.

Overlay: 
    Overlays are environment-specific customizations. This is where if you want to define different settings for each enviorments
    For example, if you want a dev overlay that adjusts the number of replicas and the image tag for a development environment, and a prod overlay for production.

Patches
    Patches in Kustomize are used to modify the original resources.  A patch file contains the changes you want to apply to the resource, such as updating the number of replicas or replacing the image tag.

Variables
    Kustomize allows you to parameterize values using environment variables. You can define values that are replaced during the build process.