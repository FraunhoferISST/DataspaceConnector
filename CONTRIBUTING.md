# Contributing to the Dataspace Connector

The following is a set of guidelines for contributing to The Dataspace Connector. This is an ongoing 
project of the  [Data Economy](https://www.isst.fraunhofer.de/en/business-units/data-economy.html) 
business unit of the [Fraunhofer ISST](https://www.isst.fraunhofer.de/en.html) hosted on 
[GitHub](https://github.com/FraunhoferISST/Dataspace-Connector). You are very welcome to contribute 
to this project when you find a bug, want to suggest an improvement, or have an idea for a useful 
feature. For this, always create an issue and a corresponding branch, and follow our style 
guides as described below.

Please note that we have a [code of conduct](CODE_OF_CONDUCT.md) that all developers should stick to.

## Changelog

We document changes in the [CHANGELOG.md](CHANGELOG.md) on root level which is formatted and 
maintained according to the rules documented on http://keepachangelog.com.

## Issues

You always have to create an issue if you want to integrate a bugfix, improvement, or feature. 
Briefly and clearly describe the purpose of your contribution in the corresponding issue. 
The pre-defined [labels](#labels) improve the understanding of your intentions and help to follow 
the scope of your changes. 

**Bug Report**: As mentioned above, bug reports should be submitted as an issue. To give others 
the chance to reproduce the error in order to find a solution as quickly as possible, the report 
should at least include the following information:
* Description: What did you expect and what happened instead?
* Steps to reproduce (system specs included)
* Relevant logs and/or media (optional): e.g. an image

## Labels

The [labels](https://github.com/FraunhoferISST/DataspaceConnector/labels) are listed at the 
[issues](https://github.com/FraunhoferISST/DataspaceConnector/issues). 
There are three types of labels: one describes the content of the issue and should be used by the 
developer that creates the issue. The other one, starting with `status`, will be added from the 
developer that takes on the issue. New issues should be initially marked with `status:open`. 
Furthermore, the issues `core-functionality` and `ids-functionality` help to specify the scope of 
the issue. They map the structure of the roadmap. 
*  Basic labels: `bug`, `enhancement`, `suggestion`, `documentation` `outdated`, `question`, `discussion`
*  `status:closed`: issue is closed (after successful approval by issuer and QA)
*  `status:duplicate`: issue is a duplicate of another linked issue and therefore discontinued
*  `status:in-progress`: issue has been assigned and is currently being worked on
*  `status:on-hold`: issue may be implemented at a later date
*  `status:open`: issue has been submitted or re-opened recently
*  `status:out-of-scope`: issue is considered out of the project's scope and therefore not further considered
*  `status:resolved`: issue has been implemented and tested by a developer
*  `status:wont-fix`: issue is in scope but considered impossible or too expensive to deal with

## Branches

This repository has a `dev` branch in addition to the `master` branch. The idea is to always 
merge other branches into the `dev` branch (as SNAPSHOT version) and to push the changes from 
there into the `master` only for releases. This way, the `dev` branch is always up to date, 
with the risk of small issues, while the `master` only contains official releases.

After creating an issue yourself or if you want to address an existing issue, you have to create a 
branch with a unique number and name that assigns it to an issue. Therefore, follow the guidelines 
at https://deepsource.io/blog/git-branch-naming-conventions/. After your changes, update the 
`README.md`, Wiki, and `CHANGELOG.md` with necessary details. Then, create a pull request and note 
that **committing to the master is not allowed**. Please use the feature `linked issues` to link 
issues and pull requests. 

## Commits

We encourage all contributors to stick to the commit convention following the specification on 
[Conventional Commits](https://www.conventionalcommits.org/en/v1.0.0/). In general, use  the 
imperative in the present tense. A quick overview of the schema:
```
<type>[optional scope]: <description>
[optional body]
[optional footer(s)]
```

Types: `fix`, `feat`, `chore`, `test`, `refactor`, `docs`, `release`. Append `!` for breaking 
changes to a type. 

An example of a very good commit might look like this: `feat![login]: add awesome breaking feature`

**Pay attention to never push your IDS keystore or certificate to the repository - not in a single 
commit! Therefore, the `resources/conf` directory is added to the `.gitignore`.**

## Versioning
The Dataspace Connector uses the [SemVer](https://semver.org/) for versioning. The release versions 
are tagged with their respective version.
