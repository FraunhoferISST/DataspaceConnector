# Contributing to the Dataspace Connector

The following is a set of guidelines for contributing to The Dataspace Connector. This is an ongoing project of the  [Data Economy](https://www.isst.fraunhofer.de/en/business-units/data-economy.html) business unit of the [Fraunhofer ISST](https://www.isst.fraunhofer.de/en.html) hosted on [GitHub](https://github.com/FraunhoferISST/Dataspace-Connector). You are very welcome to contribute to this project when you find a bug, want to suggest an improvement, or have an idea for a useful feature. For this, always create an issue and a corresponding pull request, and follow our style guides as described below.

Please note that we have a [code of conduct](#code-of-conduct) that all developers should stick to.

## Changelog

We document changes in the [CHANGELOG.md](CHANGELOG.md) on root level which is formatted and maintained according to the rules documented on http://keepachangelog.com.

## Issues

You always have to create an issue if you want to integrate a bugfix, improvement, or feature. Briefly and clearly describe the purpose of your contribution in the corresponding issue. The pre-defined [labels](#labels) improve the understanding of your intentions and help to follow the scope of your changes. 

**Bug Report**: As mentioned above, bug reports should be submitted as an issue. To give others the chance to reproduce the error in order to find a solution as quickly as possible, the report should at least include the following information:
* Description: What did you expect and what happened instead?
* Steps to reproduce (system specs included)
* Relevant logs and/or media (optional): e.g. an image

## Labels

The labels are also listed at the menu item `Issues`. There are two types of labels: one describes the content of the issue and should be used by the developer that creates the issue. The other one, starting with `status`, will be added from the developer that takes on the issue. New issues should be initially marked with `status:open`.
*  Basic labels: `bug`, `enhancement`, `suggestion`, `documentation` `outdated`, `question`, `discussion`
*  `status:closed`: issue is closed (after successful approval by issuer and QA)
*  `status:duplicate`: issue is a duplicate of another linked issue and therefore discontinued
*  `status:in-progress`: issue has been assigned and is currently being worked on
*  `status:open`: issue has been submitted or re-opened recently
*  `status:out-of-scope`: issue is considered out of the project's scope and therefore not further considered
*  `status:resolved`: issue has been implemented and tested by a developer
*  `status:wont-fix`: issue is in scope but considered impossible or too expensive to deal with

## Branches

After creating an issue yourself or if you want to address an existing issue, you have to create a branch with a unique number and name that assigns it to an issue. Therefore, follow the guidelines at https://deepsource.io/blog/git-branch-naming-conventions/. After your changes, update the README.md and CHANGELOG.md with details of changes. Then, create a pull request and note that **committing to the master is not allowed**. Please use the feature `Linked issues` to link issues and pull requests. 

## Commits

We encourage all contributors to stick to the commit convention following the specification on [Conventional Commits](https://www.conventionalcommits.org/en/v1.0.0/). In general, use  the imperative in the present tense. A quick overview of the schema:
```
<type>[optional scope]: <description>
[optional body]
[optional footer(s)]
```

Types: `fix`, `feat`, `chore`, `test`, `refactor`, `docs`, `release`. Append `!` for breaking changes to a type. 

An example of a very good commit might look like this: `feat![login]: add awesome breaking feature`

**Pay attention to never push your IDS keystore or certificate to the repository - not in a single commit! Therefore, the `resources/conf` directory is added to the `.gitignore`.**

## Versioning
The Dataspace Connector uses the [SemVer](https://semver.org/) for versioning. The release versions are tagged with their respective version.

## Code of Conduct

### Our Pledge

In the interest of fostering an open and welcoming environment, we as
contributors and maintainers pledge to making participation in our project and
our community a harassment-free experience for everyone, regardless of age, body
size, disability, ethnicity, gender identity and expression, level of experience,
nationality, personal appearance, race, religion, or sexual identity and
orientation.

### Our Standards

Examples of behavior that contributes to creating a positive environment
include:

* Using welcoming and inclusive language
* Being respectful of differing viewpoints and experiences
* Gracefully accepting constructive criticism
* Focusing on what is best for the community
* Showing empathy towards other community members

Examples of unacceptable behavior by participants include:

* The use of sexualized language or imagery and unwelcome sexual attention or
advances
* Trolling, insulting/derogatory comments, and personal or political attacks
* Public or private harassment
* Publishing others' private information, such as a physical or electronic
  address, without explicit permission
* Other conduct which could reasonably be considered inappropriate in a
  professional setting

### Our Responsibilities

Project maintainers are responsible for clarifying the standards of acceptable
behavior and are expected to take appropriate and fair corrective action in
response to any instances of unacceptable behavior.

Project maintainers have the right and responsibility to remove, edit, or
reject comments, commits, code, wiki edits, issues, and other contributions
that are not aligned to this Code of Conduct, or to ban temporarily or
permanently any contributor for other behaviors that they deem inappropriate,
threatening, offensive, or harmful.

### Scope

This Code of Conduct applies both within project spaces and in public spaces
when an individual is representing the project or its community. Examples of
representing a project or community include using an official project e-mail
address, posting via an official social media account, or acting as an appointed
representative at an online or offline event. Representation of a project may be
further defined and clarified by project maintainers.

### Enforcement

Instances of abusive, harassing, or otherwise unacceptable behavior may be
reported by contacting the project team. All
complaints will be reviewed and investigated and will result in a response that
is deemed necessary and appropriate to the circumstances. The project team is
obligated to maintain confidentiality with regard to the reporter of an incident.
Further details of specific enforcement policies may be posted separately.

Project maintainers who do not follow or enforce the Code of Conduct in good
faith may face temporary or permanent repercussions as determined by other
members of the project's leadership.

### Attribution

This Code of Conduct is adapted from the [Contributor Covenant](http://contributor-covenant.org), version 1.4, available at http://contributor-covenant.org/version/1/4.
